package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

public class ParameterId extends Identifier {

  /* type: DataTypeId corresponding to the type of
   *       the represented parameter */
  private final DataTypeId type;

  public ParameterId(IdentifierNode node, DataTypeId type) {
    super(node);
    this.type = type;
  }

  @Override
  public DataTypeId getType() {
    return type;
  }

  @Override
  public String toString() {
    return type + " IDENTIFIER for '" + super.getNode() + "'";
  }
}

