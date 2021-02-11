package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public abstract class ExpressionNode extends AssignRHSNode {

  public ExpressionNode(int line, int charPositionInLine) {
    super(line, charPositionInLine);
  }

  public abstract DataTypeId getType(SymbolTable symbolTable);

  @Override
  public abstract String toString();
}