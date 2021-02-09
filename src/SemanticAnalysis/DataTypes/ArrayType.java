package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public class ArrayType extends DataTypeId {

  private int size;
  private final DataTypeId arrayType;

  public ArrayType(DataTypeId arrayType) {
    super();
    this.arrayType = arrayType;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public DataTypeId getArrayType() {
    return this.arrayType;
  }

  @Override
  public String toString() {
    return arrayType.toString() + "[]";
  }
}
