package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class AssignVarNode extends StatementNode {

  private final AssignLHSNode left;
  private final AssignRHSNode right;

  public AssignVarNode(AssignLHSNode left, AssignRHSNode right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    left.semanticAnalysis(symbolTable, errorMessages);
    right.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId leftType = left.getType(symbolTable);
    DataTypeId rightType = right.getType(symbolTable);

    if (leftType == null) {
      errorMessages.add(left.getLine() + ":" + left.getCharPositionInLine()
              + " Could not resolve type of left hand side assignment. ");
    } else if (rightType == null) {
      errorMessages.add(right.getLine() + ":" + right.getCharPositionInLine()
              + " Could not resolve type of right hand side assignment. ");
    } else if (!leftType.equals(rightType)) {
      errorMessages.add(left.getLine() + ":" + left.getCharPositionInLine()
          + " Assignment to: " + left.toString() + "must be of type " + leftType +
          " not " + rightType);
    }
  }
}