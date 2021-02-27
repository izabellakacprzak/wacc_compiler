package AbstractSyntaxTree;

import InternalRepresentation.InternalState;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public interface ASTNode {

  /* Method to check for semantic errors, and to add any found errors to the errorMessages List.
   * The symbolTable is the current scope of declared Identifiers */
  void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages);
  void generateAssembly(InternalState internalState);
}