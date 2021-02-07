package AbstractSyntaxTree.statement;

public class SkipStatementNode extends StatementNode {
  public String toString() {
    return "skip";
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
