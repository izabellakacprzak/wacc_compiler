package SemanticAnalysis;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;

public class VariableId extends Identifier {

  private final DataTypeId type;

  public VariableId(ASTNode node, DataTypeId type) {
    super(node);
    this.type = type;
  }

  public DataTypeId getType() {
    return type;
  }

  @Override
  public String toString() {
    return type + " IDENTIFIER for '" + ((IdentifierNode) super.getNode()).getIdentifier() + "'";
  }
}