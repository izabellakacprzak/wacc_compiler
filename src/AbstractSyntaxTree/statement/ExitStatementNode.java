package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class ExitStatementNode extends StatementNode {
  private ExpressionNode expr;

  public ExitStatementNode(ExpressionNode expr) {
    this.expr = expr;
  }

  @Override
  public boolean hasExitStatement() {
    return true;
  }
}