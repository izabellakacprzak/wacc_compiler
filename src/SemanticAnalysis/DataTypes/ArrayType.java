package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class ArrayType extends DataTypeId {

  private int size;
  private final DataTypeId elemType;

  public ArrayType(DataTypeId elemType) {
    super();
    this.elemType = elemType;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public DataTypeId getElemType() {
    return elemType;
  }

  @Override
  public String toString() {
    return elemType + "[]";
  }

  @Override
  public boolean equals(Object object) {
    if ((object instanceof DataTypeId) && this.elemType == null) {
      return true;
    }

    if (object instanceof ArrayType) {
      ArrayType arrayObject = (ArrayType) object;
      return arrayObject.getElemType() == null || arrayObject.getElemType().equals(this.elemType);
    }
    return false;
  }
}
