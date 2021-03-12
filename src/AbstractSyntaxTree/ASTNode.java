package AbstractSyntaxTree;

import InternalRepresentation.InternalState;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public interface ASTNode {

  /* Method to check for semantic errors, and to add any found errors to the errorMessages List.
   * The symbolTable is the current scope of declared Identifiers */
  void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages);

  /* Method to generate assembly Instructions and store them in the internalState.
   * Calls the appropriate function in the internalState's CodeGenVisitor */
  void generateAssembly(InternalState internalState);

  /* All nodes must have a getter and a setter for their current SymbolTable */
  void setCurrSymTable(SymbolTable currSymTable);

  SymbolTable getCurrSymTable();
}