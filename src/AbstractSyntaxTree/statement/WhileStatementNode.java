package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;

public class WhileStatementNode extends StatementNode {
  private final ExpressionNode condition;
  private final StatementNode statement;

  public WhileStatementNode(ExpressionNode condition, StatementNode statement) {
    this.condition = condition;
    this.statement = statement;
  }

  @Override
  public boolean hasReturnStatement() {
    return statement.hasReturnStatement();
  }

  @Override
  public boolean hasExitStatement() {
    return statement.hasExitStatement();
  }

  @Override
  public void setReturnType(DataTypeId returnType) {
    statement.setReturnType(returnType);
  }
}