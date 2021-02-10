package SemanticAnalysis;

import AbstractSyntaxTree.ASTNode;
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

  @Override
  public String toString() {
    StringBuilder string = new StringBuilder();

    string.append(returnType.toString());
    string.append(" <identifier>(");

    for (int i = 0; i < params.size(); i++) {
      string.append(params.get(i).toString());

      if (i != params.size() - 1) {
        string.append(", ");
      }
    }

    string.append(")");

    return string.toString();
  }
}
