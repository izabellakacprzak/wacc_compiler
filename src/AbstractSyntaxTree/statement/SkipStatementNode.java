package AbstractSyntaxTree.statement;

public class SkipStatementNode extends StatementNode {
  public String toString() {
    return "skip";
  }

  public boolean hasReturnStatement() {
    return false;
  }
}
