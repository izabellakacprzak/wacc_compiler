package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
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
  private boolean hasObject;

  public ParamListNode(List<IdentifierNode> identifiers, List<TypeNode> types) {
    this.identifiers = identifiers;
    this.types = types;
    this.hasObject = false;
  }

  /* Create a new ParameterId for each parameter and add to the symbolTable.
   * Returns the ParameterIds as a list  */
  public List<ParameterId> getIdentifiers(SymbolTable symbolTable) {
    List<ParameterId> paramIds = new ArrayList<>();

    for (int i = 0; i < identifiers.size(); i++) {
      ParameterId parameter;
      if (i < types.size()) {
        parameter = new ParameterId(identifiers.get(i), types.get(i).getType());
      } else {
        parameter = new ParameterId(identifiers.get(i));
      }
      paramIds.add(parameter);
      symbolTable.add(identifiers.get(i).getIdentifier(), parameter);
    }

    return paramIds;
  }


  public List<ParameterId> getParameterIds() {
    List<ParameterId> paramIds = new ArrayList<>();

    for (IdentifierNode identifier : identifiers) {
      Identifier id = currSymTable.lookupAll(identifier.getIdentifier());
      if (id instanceof ParameterId) {
        paramIds.add((ParameterId) id);
      }
    }

    return paramIds;
  }

  public void addObject() {
    this.hasObject = true;
  }

  public List<DataTypeId> getParamTypes() {
    return types.stream().map(TypeNode::getType)
        .collect(Collectors.toList());
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on each identifier */
    for (IdentifierNode identifier : identifiers) {
      identifier.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }

    /* Recursively call semanticAnalysis on each type */
    for (TypeNode type : types) {
      type.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitParamListNode(internalState, identifiers, currSymTable,
        hasObject);
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
