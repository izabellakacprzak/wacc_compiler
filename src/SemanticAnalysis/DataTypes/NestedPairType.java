package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class NestedPairType extends DataTypeId {

  @Override
  public boolean equals(Object object) {
    if (object instanceof PairType) {
      PairType pairObject = (PairType) object;
      return pairObject.toString().equals(this.toString());
    }

    return false;
  }

  @Override
  public String toString() {
    return "pair";
  }
}
