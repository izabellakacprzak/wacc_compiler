package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.DataTypes.ArrayType;

import java.util.ArrayList;
import java.util.List;

public class ParameterId extends Identifier {

  /* type: DataTypeId corresponding to the type of
   *       the represented parameter */
  private final List<ParameterId> matchingParams = new ArrayList<>();
  private DataTypeId type;
  private List<DataTypeId> expectedTypes = new ArrayList<>();

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

  public void setBaseElemType(DataTypeId baseType) {
    ArrayType arrType;
    DataTypeId type = this.type;
    int count = 0;
    while (type instanceof ArrayType) {
      arrType = (ArrayType) type;
      type = arrType.getElemType();
      count++;
    }

    setNestedType(baseType, count);
  }

  public void setNestedType(DataTypeId baseType, int depth) {
    DataTypeId type = baseType;
    while (depth > 0) {
      type = new ArrayType(type);
      depth--;
    }

    this.type = type;
  }



  public boolean isUnsetArray() {
    ArrayType arrType;
    DataTypeId type = getType();
    while (type instanceof ArrayType) {
      arrType = (ArrayType) type;
      type = arrType.getElemType();
    }

    return type == null;
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

  public void addToExpectedTypes(List<DataTypeId> types) {
    List<DataTypeId> expectedTypes = new ArrayList<>();

    for (DataTypeId type : types) {
      for (DataTypeId expected : this.expectedTypes) {
        if (expected.equals(type)) {
          expectedTypes.add(expected);
        }
      }
    }

    if (expectedTypes.size() == 1) {
      this.type = expectedTypes.get(0);
    }

    this.expectedTypes = expectedTypes;
  }

  @Override
  public String toString() {
    return type + " IDENTIFIER for '" + super.getNode() + "'";
  }
}

