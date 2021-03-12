package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ArrayTypeNode implements TypeNode {

  /* type:         TypeNode of the corresponding type of elements the array contains
   * currSymTable: set to the node's SymbolTable for the current scope during semanticAnalysis */
  private final TypeNode type;
  private SymbolTable currSymTable = null;

  public ArrayTypeNode(TypeNode type) {
    this.type = type;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on type */
    type.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
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
    return new ArrayType((DataTypeId) type.getIdentifier(symbolTable));
  }

  @Override
  public DataTypeId getType() {
    return new ArrayType(type.getType());
  }
}