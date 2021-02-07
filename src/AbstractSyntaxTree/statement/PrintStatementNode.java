package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class PrintStatementNode extends StatementNode {
  private final ExpressionNode expression;

  public PrintStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public boolean hasReturnStatement() {
    return false;
  }

  @Override
  public boolean hasNoStatementAfterReturn() {
    return true;
  }

  @Override
  public boolean hasExitStatement() {
    return false;
  }
}