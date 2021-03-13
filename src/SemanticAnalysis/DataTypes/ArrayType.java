package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

import javax.xml.crypto.Data;

public class ArrayType extends DataTypeId {

  /* ARRAY_BYTES_SIZE: size of a reference of an array */
  private static final int ARRAY_BYTES_SIZE = 4;

  /* elemType:  DataTypeId of the corresponding type of elements the array contains */
  private DataTypeId elemType;

  public ArrayType(DataTypeId elemType) {
    this.elemType = elemType;
  }

  public ArrayType() {

  }

  public DataTypeId getElemType() {
    return elemType;
  }

  public void setElemType(DataTypeId elemType) {
    this.elemType = elemType;
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
  public String toString() {
    return elemType + "[]";
  }

  /* Returns corresponding size of an array reference in bytes */
  @Override
  public int getSize() {
    return ARRAY_BYTES_SIZE;
  }
}
