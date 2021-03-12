package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.assignment.FuncCallNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.OverloadFuncId;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class AssignVarNode extends StatementNode {

  /* left:         AssignLHSNode corresponding to what being assigned to
   * right:        AssignRHSNode corresponding to the assignment for the AssignLHSNode */
  private final AssignLHSNode left;
  private final AssignRHSNode right;

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
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on LHS node */
    left.semanticAnalysis(symbolTable, errorMessages);
    right.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the left assignment type and the right assignment type
     * can be resolved and match */
    DataTypeId leftType = left.getType(symbolTable);
    DataTypeId rightType = null;

    if (leftType == null) {
      if (varHasBeenDeclared(errorMessages, left)) {
        errorMessages.add(left.getLine() + ":" + left.getCharPositionInLine()
            + " Could not resolve type of LHS assignment '" + left + "'.");
      }
      return;
    }

    List<DataTypeId> returnTypes;

    if(right instanceof FuncCallNode
        && ((FuncCallNode) right).getIdentifier(symbolTable) instanceof OverloadFuncId) {
      returnTypes = ((FuncCallNode) right).getOverloadType(symbolTable);
      for(DataTypeId returnType : returnTypes) {
        if(returnType == null) {
          continue;
        }

        if(returnType.equals(leftType)) {
          rightType = returnType;
          break;
        }
      }

      if(rightType == null) {
        errorMessages.add(right.getLine() + ":" + right.getCharPositionInLine()
            + " RHS type does not match LHS type for assignment.'"
            + " Expected: " + leftType + ". Could not find matching return type"
            + " in overloaded functions.");
        return;
      } else {
        ((FuncCallNode) right).setReturnType(rightType);
      }
    } else {
      rightType = right.getType(symbolTable);
    }

    if (rightType == null) {
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
    internalState.getCodeGenVisitor().
        visitAssignVarNode(internalState, left, right, getCurrSymTable());
  }

}

