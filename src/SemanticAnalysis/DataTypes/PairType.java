package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public class PairType extends DataTypeId {

  private final DataTypeId fstType;
  private final DataTypeId sndType;

  public PairType(ASTNode node, DataTypeId fstType, DataTypeId sndType) {
    super(node);
    this.fstType = fstType;
    this.sndType = sndType;
  }

  public DataTypeId getFstType() {
    return fstType;
  }

  public DataTypeId getSndType() {
    return sndType;
  }

  @Override
  public String toString() {
    return "pair(" + fstType.toString() + ", " + sndType.toString() + ")";
  }

  @Override
  public boolean equals(DataTypeId object) {

    if (object instanceof PairType) {
      PairType pairObject = (PairType) object;
      return pairObject.getFstType().equals(this.fstType) &&
              pairObject.getSndType().equals(this.sndType);
    }

    return false;
  }
}