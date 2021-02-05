package SemanticAnalysis;

import AbstractSyntaxTree.ASTNode;

public class ParameterId extends Identifier {

  private final DataTypeId type;

  public ParameterId(ASTNode node, DataTypeId type) {
    super(node);
    this.type = type;
  }

  @Override
  public String toString() {
    return type.toString() + super.getNode().toString();
  }
}

