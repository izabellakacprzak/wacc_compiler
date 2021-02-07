package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class ReturnStatementNode extends StatementNode {
  private final ExpressionNode returnExpr;

  public ReturnStatementNode(ExpressionNode returnExpr) {
    this.returnExpr = returnExpr;
  }

  @Override
  public boolean hasReturnStatement() {
    return true;
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