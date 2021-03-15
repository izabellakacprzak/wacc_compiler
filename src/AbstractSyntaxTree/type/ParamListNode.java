package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParamListNode implements ASTNode {

  /* identifiers:  List of IdentifierNodes corresponding to each of a function's parameters
   * types:        List of TypeNodes corresponding to each of a function's parameters
   * currSymTable: set to the node's SymbolTable for the current scope during semanticAnalysis */
  private final List<IdentifierNode> identifiers;
  private final List<TypeNode> types;
  private SymbolTable currSymTable = null;

  public ParamListNode(List<IdentifierNode> identifiers, List<TypeNode> types) {
    this.identifiers = identifiers;
    this.types = types;
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

  public List<DataTypeId> getParamTypes() {
    return types.stream().map(TypeNode::getType)
        .collect(Collectors.toList());
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on each identifier */
    for (IdentifierNode identifier : identifiers) {
      identifier.semanticAnalysis(symbolTable, errorMessages);
    }

    /* Recursively call semanticAnalysis on each type */
    for (TypeNode type : types) {
      type.semanticAnalysis(symbolTable, errorMessages);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitParamListNode(internalState, identifiers, currSymTable);
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }
}
