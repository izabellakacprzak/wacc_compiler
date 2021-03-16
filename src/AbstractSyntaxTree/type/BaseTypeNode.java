package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class BaseTypeNode implements TypeNode {

  /* type:         BaseType.Type enum corresponding to the base type this represents
   * currSymTable: set to the node's SymbolTable for the current scope during semanticAnalysis */
  private final Type baseType;
  private SymbolTable currSymTable = null;

  public BaseTypeNode(Type baseType) {
    this.baseType = baseType;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return symbolTable.lookupAll(baseType.toString().toLowerCase());
  }

  @Override
  public DataTypeId getType() {
    return new BaseType(baseType);
  }
}