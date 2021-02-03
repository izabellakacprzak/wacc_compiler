package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public class StringType extends DataTypeId {

  public StringType(ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "string";
  }
}
