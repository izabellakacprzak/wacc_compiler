package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public class PairType extends DataTypeId {

  private final DataTypeId fstType;
  private final DataTypeId sndType;

  public PairType(DataTypeId fstType, DataTypeId sndType) {
    super();
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
}