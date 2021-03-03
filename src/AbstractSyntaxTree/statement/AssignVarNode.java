package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.assignment.PairElemNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Enums.*;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.Instructions.StrInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

import java.util.List;

import static InternalRepresentation.Enums.ConditionCode.VS;
import static InternalRepresentation.Enums.LdrType.LDR;

public class AssignVarNode extends StatementNode {

  private static final int BYTE_SIZE = 1;
  private static final int ADDRESS_BYTE_SIZE = 4;

  /* left:  AssignLHSNode corresponding to what being assigned to
   * right: AssignRHSNode corresponding to the assignment for the AssignLHSNode */
  private final AssignLHSNode left;
  private final AssignRHSNode right;
  private SymbolTable currSymTable = null;

  public AssignVarNode(AssignLHSNode left, AssignRHSNode right) {
    this.left = left;
    this.right = right;
  }

  /* Check whether the inability to resolve the assignment does not stem from an undeclared variable.
   * true if variable has been declared in the current scope. */
  public boolean varHasBeenDeclared(List<String> errorMessages, AssignLHSNode node) {
    if (errorMessages.isEmpty()) {
      return true;
    }

    String lastErrorMsg = errorMessages.get(errorMessages.size() - 1);
    String line = Integer.toString(node.getLine());
    String charPos = Integer.toString(node.getCharPositionInLine());

    return (!lastErrorMsg.contains(line + ":" + charPos) || !lastErrorMsg.contains("Identifier"));
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on LHS node */
    left.semanticAnalysis(symbolTable, errorMessages);
    right.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the left assignment type and the right assignment type
     * can be resolved and match */
    DataTypeId leftType = left.getType(symbolTable);
    DataTypeId rightType = right.getType(symbolTable);

    if (leftType == null) {
      if (varHasBeenDeclared(errorMessages, left)) {
        errorMessages.add(left.getLine() + ":" + left.getCharPositionInLine()
            + " Could not resolve type of LHS assignment '" + left + "'.");
      }
    } else if (rightType == null) {
      if (varHasBeenDeclared(errorMessages, right)) {
        errorMessages.add(right.getLine() + ":" + right.getCharPositionInLine()
            + " Could not resolve type of RHS assignment'" + right + "'.");
      }
    } else if (!leftType.equals(rightType) && !stringToCharArray(leftType, rightType)) {
      errorMessages.add(left.getLine() + ":" + left.getCharPositionInLine()
          + " RHS type does not match LHS type for assignment. "
          + " Expected: " + leftType + " Actual: " + rightType);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    right.generateAssembly(internalState);
    Register rightNodeResult = internalState.popFreeRegister();

    if (left instanceof IdentifierNode) {
      int typeSize = left.getType(currSymTable).getSize();
      int offset = currSymTable.getOffset(((IdentifierNode) left).getIdentifier());

      StrType strType = typeSize == BYTE_SIZE ? StrType.STRB : StrType.STR;
      internalState
          .addInstruction(new StrInstruction(strType, rightNodeResult, Register.SP, offset));

    } else if (left instanceof PairElemNode) {
      PairElemNode pairElem = (PairElemNode) left;
      Register leftNodeResult = internalState.peekFreeRegister();

      String pairID = pairElem.getIdentifier();
      int offset = currSymTable.getOffset(pairID) + pairElem.getPosition() * ADDRESS_BYTE_SIZE;

      internalState
          .addInstruction(new LdrInstruction(LdrType.LDR, leftNodeResult, Register.SP, offset));
      internalState.addInstruction(new MovInstruction(Register.R0, leftNodeResult));

      internalState
          .addInstruction(new BranchInstruction(BranchOperation.BL, BuiltInFunction.NULL_POINTER));

      internalState.addInstruction(new LdrInstruction(LdrType.LDR, leftNodeResult, leftNodeResult,
          pairElem.getPosition() * ADDRESS_BYTE_SIZE));

      StrType strType =
          pairElem.getType(currSymTable).getSize() == BYTE_SIZE ? StrType.STRB : StrType.STR;
      internalState.addInstruction(new StrInstruction(strType, rightNodeResult, leftNodeResult));

    } else if (left instanceof ArrayElemNode) {
      Register arrayReg = internalState.popFreeRegister();

      ArrayElemNode arrayElem = (ArrayElemNode) left;
      arrayElem.generateElemAddr(internalState, arrayReg);

      StrType strType =
          arrayElem.getType(currSymTable).getSize() == BYTE_SIZE ? StrType.STRB : StrType.STR;
      internalState.addInstruction(
          new StrInstruction(strType, rightNodeResult, arrayReg));

      internalState.pushFreeRegister(arrayReg);
    }

    internalState.pushFreeRegister(rightNodeResult);
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}

