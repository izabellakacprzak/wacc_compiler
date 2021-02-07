package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignLHSNode;

public class IdentifierNode extends ExpressionNode implements AssignLHSNode {
  private final String identifier;

  public IdentifierNode(String identifier){
    this.identifier = identifier;
  }
}
