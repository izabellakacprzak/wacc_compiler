package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;

public class StatementNode implements ASTNode {

  public boolean hasReturnStatement() {
    return false;
  }

  public boolean hasNoStatementAfterReturn() {
    return true;
  }

  public boolean hasExitStatement() {
    return false;
  }
}
