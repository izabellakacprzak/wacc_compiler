package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

public abstract class Identifier {

  /* Store node associated with Identifier for accessing identifier string */
  private final IdentifierNode node;

  public Identifier(IdentifierNode node) {
    this.node = node;
  }

  public IdentifierNode getNode() {
    return node;
  }

  /* All identifiers must Override toString for use in error messages */
  @Override
  public abstract String toString();
}