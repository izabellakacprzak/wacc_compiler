package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Enums.ArithmeticOperation;
import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.ArithmeticInstruction;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.InternalState;
import InternalRepresentation.Operand;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReadStatementNode extends StatementNode {

  private static final boolean SET_BITS = true;
  private static final int NO_OFFSET = 0;

  /* assignment:  AssignLHSNode corresponding to the IdentifierNode, ArrayElemNode
   *                or PairElemNode 'read' was called with */
  private final AssignLHSNode assignment;
  private SymbolTable currSymTable = null;

  public ReadStatementNode(AssignLHSNode assignment) {
    this.assignment = assignment;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on assignment node */
    assignment.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the type of assignment is an INT or a CHAR */
    DataTypeId assignmentType = assignment.getType(symbolTable);

    if (assignmentType == null) {
      errorMessages.add(assignment.getLine() + ":" + assignment.getCharPositionInLine()
          + " Could not resolve type for '" + assignment + "."
          + " Expected: INT, CHAR");

    } else if (!assignmentType.equals(new BaseType(BaseType.Type.INT)) &&
        !assignmentType.equals(new BaseType(BaseType.Type.CHAR))) {
      errorMessages.add(assignment.getLine() + ":" + assignment.getCharPositionInLine()
          + " Incompatible type for 'read' statement."
          + " Expected: INT, CHAR Actual: " + assignmentType);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    Register nextAvailable = internalState.peekFreeRegister();

    if (assignment instanceof IdentifierNode) {
      String identifier = ((IdentifierNode) assignment).getIdentifier();

      int offset = currSymTable.getOffset(identifier);
      internalState.addInstruction(new ArithmeticInstruction(ArithmeticOperation.ADD, nextAvailable, Register.R0,
              new Operand(offset), !SET_BITS));
    } else {
      assignment.generateAssembly(internalState);
      internalState.addInstruction(new ArithmeticInstruction(ArithmeticOperation.ADD, nextAvailable, Register.R0,
              new Operand(0), !SET_BITS));
    }

    internalState.addInstruction(new MovInstruction(Register.R0, nextAvailable));
    DataTypeId type = assignment.getType(currSymTable);

    if (type instanceof BaseType) {
      BaseType.Type baseType = ((BaseType) type).getBaseType();

      switch (baseType) {
        case INT:
          BuiltInFunction.READ_INT.setUsed();
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                  BuiltInFunction.READ_INT.getLabel()));
          break;
        case CHAR:
          BuiltInFunction.READ_CHAR.setUsed();
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                  BuiltInFunction.READ_CHAR.getLabel()));
          break;
      }
    }
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}