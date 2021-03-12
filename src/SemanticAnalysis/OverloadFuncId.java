package SemanticAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OverloadFuncId extends Identifier {

  private final List<FunctionId> functions = new ArrayList<>();

  public OverloadFuncId(FunctionId function) {
    super();
    functions.add(function);
  }

  public boolean addNewFunc(FunctionId function) {
    if (!this.canOverload(function)) {
      return false;
    }

    functions.add(function);
    return true;
  }

  public FunctionId findFunc(List<DataTypeId> paramTypes) {
    for (FunctionId function : functions) {
      List<DataTypeId> funcParams = function.getParamTypes();
      if (paramTypes.equals(funcParams)) {
        return function;
      }
    }
    return null;
  }

  public FunctionId fundFuncReturnType(List<DataTypeId> paramTypes, DataTypeId returnType) {
    for (FunctionId function : functions) {
      List<DataTypeId> funcParams = function.getParamTypes();
      if (paramTypes.equals(funcParams) && returnType.equals(function.getType())) {
        return function;
      }
    }
    return null;
  }

  public int getIndex(FunctionId functionId) {
    return functions.indexOf(functionId);
  }

  public List<DataTypeId> findReturnTypes(List<DataTypeId> paramTypes) {
    List<DataTypeId> returnTypes = new ArrayList<>();
    for (FunctionId function : functions) {
      List<DataTypeId> funcParams = function.getParamTypes();
      if (paramTypes.equals(funcParams)) {
        returnTypes.add(function.getType());
      }
    }
    return returnTypes;
  }

  private boolean canOverload(FunctionId function) {
    DataTypeId returnType = function.getType();
    Set<DataTypeId> params = new HashSet<>(function.getParamTypes());

    boolean canOverload = true;

    for (FunctionId declaredFunc : functions) {

      if (declaredFunc.getType().equals(returnType)) {
        Set<DataTypeId> declaredParams = new HashSet<>(declaredFunc.getParamTypes());
        if (declaredParams.equals(params)) {
          canOverload = false;
          break;
        }
      }
    }

    return canOverload;
  }

  @Override
  public DataTypeId getType() {
    return functions.get(0).getType();
  }

  @Override
  public int getSize() {
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Overloaded functions: [");

    for (FunctionId function : functions) {
      builder.append(function.toString()).append(", ");
    }

    if (!functions.isEmpty()) {
      builder.deleteCharAt(builder.length() - 1).deleteCharAt(builder.length() - 1);
    }

    builder.append("]");
    return builder.toString();
  }
}
