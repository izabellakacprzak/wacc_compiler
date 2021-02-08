package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class IfStatementNode extends StatementNode {
  private ExpressionNode condition;
  private StatementNode thenStatement;
  private StatementNode elseStatement;

  public IfStatementNode(ExpressionNode condition, StatementNode thenStatement,
                         StatementNode elseStatement) {
    this.condition = condition;
    this.thenStatement = thenStatement;
    this.elseStatement = elseStatement;
  }

  @Override
  public boolean hasReturnStatement() {
    return (thenStatement.hasReturnStatement() || thenStatement.hasExitStatement())
    && (elseStatement.hasExitStatement() || elseStatement.hasReturnStatement());
  }

  @Override
  public boolean hasExitStatement() {
    return this.hasReturnStatement();
  }
}