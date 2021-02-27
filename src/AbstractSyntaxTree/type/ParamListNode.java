package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class ParamListNode implements ASTNode {

  /* identifiers: List of IdentifierNodes corresponding to each of a function's parameters
   * types:       List of TypeNodes corresponding to each of a function's parameters */
  private final List<IdentifierNode> identifiers;
  private final List<TypeNode> types;

  private SymbolTable currSymTable = null;

  public ParamListNode(List<IdentifierNode> identifiers, List<TypeNode> types) {
    this.identifiers = identifiers;
    this.types = types;
  }

  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  /* Create a new ParameterId for each parameter and add to the symbolTable.
   * Returns the ParameterIds as a list  */
  public List<ParameterId> getIdentifiers(SymbolTable symbolTable) {
    List<ParameterId> paramIds = new ArrayList<>();

    for (int i = 0; i < identifiers.size(); i++) {
      ParameterId parameter = new ParameterId(identifiers.get(i), types.get(i).getType());
      paramIds.add(parameter);
      symbolTable.add(identifiers.get(i).getIdentifier(), parameter);
    }

    return paramIds;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on each identifier */
    for (IdentifierNode identifier : identifiers) {
      identifier.semanticAnalysis(symbolTable, errorMessages);
    }

    /* Recursively call semanticAnalysis on each type */
    for (TypeNode type : types) {
      type.semanticAnalysis(symbolTable, errorMessages);
    }

    /* Set the current symbol table */
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }
}
