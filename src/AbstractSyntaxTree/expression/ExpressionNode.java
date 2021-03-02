package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public abstract class ExpressionNode extends AssignRHSNode {

  public ExpressionNode(int line, int charPositionInLine) {
    super(line, charPositionInLine);
  }

  /* Returns the DataTypeId corresponding to the ExpressionNode's DataType
   * or the DataType it returns */
  public abstract DataTypeId getType(SymbolTable symbolTable);

  /* All ExpressionNodes must Override toString for use in error messages */
  @Override
  public abstract String toString();

}