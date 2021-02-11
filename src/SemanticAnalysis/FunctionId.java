package SemanticAnalysis;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.type.FunctionNode;
import java.util.ArrayList;
import java.util.List;

public class FunctionId extends Identifier {

  private final DataTypeId returnType;
  private final List<ParameterId> params;
  private final SymbolTable symTable;

  public FunctionId(ASTNode node, DataTypeId returnType, List<ParameterId> params,
      SymbolTable symTable) {
    super(node);
    this.returnType = returnType;
    this.symTable = symTable;
    this.params = params;

  }

  public DataTypeId getReturnType() {
    return returnType;
  }

  public List<ParameterId> getParams() {
    return params;
  }

  public SymbolTable getSymTable() {
    return symTable;
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
    String identifier = ((FunctionNode) super.getNode()).getIdentifierNode().getIdentifier();

    StringBuilder str = new StringBuilder();

    str.append(returnType.toString()).append(' ')
        .append(identifier).append(" (");

    for (ParameterId param : params) {
      str.append(param.toString()).append(", ");
    }

    if (!params.isEmpty()) {
      str.delete(str.length() - 2, str.length() - 1);
    }

    str.append(")");

    return str.toString();
  }
}
