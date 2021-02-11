package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
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

  public List<ParameterId> getIdentifiers(SymbolTable symbolTable) {
    List<ParameterId> paramIds = new ArrayList<>();
    for (int i = 0; i < identifiers.size(); i++) {

      ParameterId parameter = new ParameterId(identifiers.get(i),
          types.get(i).getType());
      paramIds.add(parameter);
      symbolTable.add(identifiers.get(i).getIdentifier(), parameter);
    }
    return paramIds;
  }
}
