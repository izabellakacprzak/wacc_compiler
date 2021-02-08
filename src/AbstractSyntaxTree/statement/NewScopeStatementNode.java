package AbstractSyntaxTree.statement;

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
}