package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.DataTypes.ArrayType;

import java.util.ArrayList;
import java.util.List;

public class ParameterId extends Identifier {

  private final List<ParameterId> matchingParams = new ArrayList<>();
  private final List<DataTypeId> expectedTypes = new ArrayList<>();
  /* type: DataTypeId corresponding to the type of
   *       the represented parameter */
  private DataTypeId type;

  public ParameterId(IdentifierNode node, DataTypeId type) {
    super(node);
    this.type = type;
  }

  public ParameterId(IdentifierNode node) {
    this(node, null);
  }

  @Override
  public DataTypeId getType() {
    return type;
  }

  /*If the parameter is of type array and the passed type is a basic type, set the type of the array's
   * elements to the basic type. Otherwise, set the type. */
  public void setType(DataTypeId type) {
    if (this.type instanceof ArrayType && !(type instanceof ArrayType)) {
      ((ArrayType) this.type).setElemType(type);
    } else {
      this.type = type;
    }
  }

  @Override
  public int getSize() {
    return type.getSize();
  }

  public void addToMatchingParams(ParameterId paramId) {
    if (!matchingParams.contains(paramId)) {
      matchingParams.add(paramId);
    }
  }

  public void addToExpectedTypes(DataTypeId type) {
    boolean contained = false;
    for (DataTypeId containedType : expectedTypes) {
      if (type.equals(containedType)) {
        contained = true;
        break;
      }
    }
    if (!contained) {
      expectedTypes.add(type);
    }
  }

  @Override
  public String toString() {
    return type + " IDENTIFIER for '" + super.getNode() + "'";
  }
}

