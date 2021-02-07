package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;

public class ReadStatementNode extends StatementNode {
  private final AssignLHSNode lhs;

  public ReadStatementNode(AssignLHSNode lhs) {
    this.lhs = lhs;
  }

  public boolean hasReturnStatement() {
    return false;
  }
}