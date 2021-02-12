package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class ArrayType extends DataTypeId {

  /* elemType:  DataTypeId of the corresponding type of elements the array contains */
  // TODO: removed size and corresponding setter and getter
  private final DataTypeId elemType;

  public ArrayType(DataTypeId elemType) {
    this.elemType = elemType;
  }

  public DataTypeId getElemType() {
    return elemType;
  }

  @Override
  public String toString() {
    return elemType + "[]";
  }

  /* ArrayTypes are equal if their element types are equal,
   * or at least one has a null element type */
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
