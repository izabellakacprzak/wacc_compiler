package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class PairType extends DataTypeId {

  /* fstType:  DataTypeId of the first element in the pair's type
   * sndType:  DataTypeId of the second element in the pair's type */
  private final DataTypeId fstType;
  private final DataTypeId sndType;

  public PairType(DataTypeId fstType, DataTypeId sndType) {
    this.fstType = fstType;
    this.sndType = sndType;
  }

  public DataTypeId getFstType() {
    return fstType;
  }

  public DataTypeId getSndType() {
    return sndType;
  }

  /* Compares possible combinations of when types in a pair are equal,
   * including a null matching with any type */
  private boolean equalsPairElem(DataTypeId el1, DataTypeId el2) {
    return el1 == null || el2 == null || el1.equals(el2);
  }

  @Override
  public String toString() {
    return "PAIR(" + fstType.toString() + ", " + sndType.toString() + ")";
  }

  /* PairTypes are equal if their first and second types are equal
   * according to equalsPairElem */
  @Override
  public boolean equals(Object object) {
    if (!(object instanceof PairType)) {
      return false;
    }

    PairType pairObject = (PairType) object;
    return equalsPairElem(pairObject.getFstType(), this.fstType) &&
        equalsPairElem(pairObject.getSndType(), this.sndType);
  }
}