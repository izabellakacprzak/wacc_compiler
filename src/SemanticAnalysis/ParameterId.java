package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

public class ParameterId extends Identifier {

  private final DataTypeId type;

  public ParameterId(IdentifierNode node, DataTypeId type) {
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

