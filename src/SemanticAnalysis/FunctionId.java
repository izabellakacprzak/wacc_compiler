package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;
import java.util.ArrayList;
import java.util.List;

public class FunctionId extends Identifier {

  /* returnType:  DataTypeId of the return type
   * params:      List of DataTypeIds corresponding to the function's parameter types */
  private final DataTypeId returnType;
  private final List<ParameterId> params;

  public FunctionId(IdentifierNode node, DataTypeId returnType, List<ParameterId> params) {
    super(node);
    this.returnType = returnType;
    this.params = params;
  }

  public DataTypeId getReturnType() {
    return returnType;
  }

  public List<DataTypeId> getParamTypes() {
    List<DataTypeId> paramTypes = new ArrayList<>();
    for (ParameterId curr : params) {
      paramTypes.add(curr.getType());
    }
    return paramTypes;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append(" ").append(super.getNode()).append("(");

    for (ParameterId param : params) {
      str.append(param.toString()).append(", ");
    }

    if (!params.isEmpty()) {
      str.deleteCharAt(str.length() - 1).deleteCharAt(str.length() - 1);
    }

    str.append(")");

    return "FUNCTION IDENTIFIER for '" + str + "'";
  }
}
