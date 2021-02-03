package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class FreeStatementNode {
  private ExpressionNode expr;
  public FreeStatementNode(ExpressionNode expr) {
    this.expr = expr;
  }
}
