package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;

public class ReadStatementNode extends StatementNode {
  private final AssignLHSNode lhs;

  public ReadStatementNode(AssignLHSNode lhs) {
    this.lhs = lhs;
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