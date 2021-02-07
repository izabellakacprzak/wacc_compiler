package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class PrintStatementNode extends StatementNode {
  private final ExpressionNode expression;

  public PrintStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  public boolean hasReturnStatement() {
    return false;
  }
}