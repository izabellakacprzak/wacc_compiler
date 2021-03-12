package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class ArrayType extends DataTypeId {

  /* ARRAY_BYTES_SIZE: size of a reference of an array */
  private static final int ARRAY_BYTES_SIZE = 4;
  private static final int HASH_PRIME = 67;

  /* elemType:  DataTypeId of the corresponding type of elements the array contains */
  private final DataTypeId elemType;

  public ArrayType(DataTypeId elemType) {
    this.elemType = elemType;
  }

  public DataTypeId getElemType() {
    return elemType;
  }

  /* ArrayTypes are equal if their element types are equal,
   * or at least one has a null element type */
  @Override
  public boolean equals(Object object) {
    if ((object instanceof DataTypeId) && this.elemType == null) {
      return true;
    }

    if (!(object instanceof ArrayType)) {
      return false;
    }

    ArrayType arrayObject = (ArrayType) object;
    return arrayObject.getElemType() == null
        || arrayObject.getElemType().equals(this.elemType);
  }

  @Override
  public int hashCode() {
    return (elemType.hashCode()) * HASH_PRIME;
  }

  @Override
  public String toString() {
    return elemType + "[]";
  }

  /* Returns corresponding size of an array reference in bytes */
  @Override
  public int getSize() {
    return ARRAY_BYTES_SIZE;
  }
}
