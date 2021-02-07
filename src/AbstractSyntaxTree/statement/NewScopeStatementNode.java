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
  public boolean hasNoStatementAfterReturn() {
    return true;
  }

  @Override
  public boolean hasExitStatement() {
    return false;
  }
}