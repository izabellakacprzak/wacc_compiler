package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public class ArrayType extends DataTypeId {

  private final int size;
  private final DataTypeId arrayType;

  public ArrayType(ASTNode node, int size, DataTypeId arrayType) {
    super(node);
    this.size = size;
    this.arrayType = arrayType;
  }

  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return arrayType.toString() + "[]";
  }
}
