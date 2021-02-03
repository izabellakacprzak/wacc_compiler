package SemanticAnalysis;

import AbstractSyntaxTree.ASTNode;

public class VariableId extends Identifier {

  private final DataTypeId type;

  public VariableId(ASTNode node, DataTypeId type) {
    super(node);
    this.type = type;
  }

  @Override
  public String toString() {
    return type.toString();
  }
}