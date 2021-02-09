package AbstractSyntaxTree.statement;

import SemanticAnalysis.DataTypeId;

public class NewScopeStatementNode extends StatementNode {
  private StatementNode statement;

  public NewScopeStatementNode(StatementNode statement) {
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