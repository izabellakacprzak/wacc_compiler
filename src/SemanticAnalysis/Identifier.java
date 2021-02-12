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

  /* Returns the DataTypeId corresponding to the Identifier's type
   * or the type it returns */
  public abstract DataTypeId getType();

  /* All identifiers must Override toString for use in error messages */
  @Override
  public abstract String toString();
}