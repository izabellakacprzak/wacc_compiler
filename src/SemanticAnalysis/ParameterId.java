package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

import java.util.ArrayList;
import java.util.List;

public class ParameterId extends Identifier {

  /* type: DataTypeId corresponding to the type of
   *       the represented parameter */
  private DataTypeId type;
  private final List<ParameterId> matchingParams = new ArrayList<>();
  private final List<DataTypeId> expectedTypes = new ArrayList<>();

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

  public void setType(DataTypeId type) {
    this.type = type;
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

