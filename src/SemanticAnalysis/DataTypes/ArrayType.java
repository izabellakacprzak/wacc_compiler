package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public class ArrayType extends DataTypeId {

  private int size = -1;
  private final DataTypeId arrayType;

  public ArrayType(ASTNode node, DataTypeId arrayType) {
    super(node);
    this.arrayType = arrayType;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) { this.size = size;}


  @Override
  public String toString() {
    return arrayType.toString() + "[]";
  }
}
