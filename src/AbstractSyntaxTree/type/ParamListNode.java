package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class ParamListNode implements ASTNode {

  private final List<IdentifierNode> identifiers;
  private final List<TypeNode> types;

  public ParamListNode(List<IdentifierNode> identifiers, List<TypeNode> types) {
    this.identifiers = identifiers;
    this.types = types;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    for (IdentifierNode identifier : identifiers) {
      identifier.semanticAnalysis(symbolTable, errorMessages);
    }

    for (TypeNode type : types) {
      type.semanticAnalysis(symbolTable, errorMessages);
    }
  }

  public List<ParameterId> getIdentifiers(SymbolTable parentSymbolTable) {
    List<ParameterId> paramIds = new ArrayList<>();
    for (int i = 0; i < identifiers.size(); i++) {
      paramIds.add(new ParameterId(identifiers.get(i),
          (DataTypeId) types.get(i).getIdentifier(parentSymbolTable)));
    }
    return paramIds;
  }

  public List<DataTypeId> getParamTypes() {
    List<DataTypeId> params = new ArrayList<>();
    for (TypeNode curr : types) {
      params.add(curr.getType());
    }
    return params;
  }
}
