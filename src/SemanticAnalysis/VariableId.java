package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

public class VariableId extends Identifier {

  private final DataTypeId type;

  public VariableId(IdentifierNode node, DataTypeId type) {
    super(node);
    this.type = type;
  }

  public DataTypeId getType() {
    return type;
  }

  @Override
  public String toString() {
    return type + " IDENTIFIER for '" + super.getNode() + "'";
  }
}