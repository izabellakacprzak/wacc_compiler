package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Enums.Register;

public abstract class Identifier {

  /* Store node associated with Identifier for accessing identifier string */
  private final IdentifierNode node;
  private Register register = null;

  public Identifier(IdentifierNode node) {
    this.node = node;
  }

  public IdentifierNode getNode() {
    return node;
  }

  public Register getRegister() {
    return register;
  }

  public void setRegister(Register register) {
    this.register = register;
  }

  /* Returns the DataTypeId corresponding to the Identifier's type
   * or the type it returns */
  public abstract DataTypeId getType();

  public abstract int getSize();
  /* All identifiers must Override toString for use in error messages */
  @Override
  public abstract String toString();
}