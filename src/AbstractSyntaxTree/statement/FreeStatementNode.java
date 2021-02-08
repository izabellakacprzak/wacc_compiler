package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class FreeStatementNode extends StatementNode {
  private ExpressionNode expr;

  public FreeStatementNode(ExpressionNode expr) {
    this.expr = expr;
  }
}
