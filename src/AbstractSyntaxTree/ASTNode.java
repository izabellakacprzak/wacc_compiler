package AbstractSyntaxTree;

import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public interface ASTNode {

  /* DEFAULT_TYPE: The default type that an unset parameter will be set to before code generation */
  DataTypeId DEFAULT_TYPE = new BaseType(INT);

  /* Method to check for semantic errors, and to add any found errors to the errorMessages List.
   * The symbolTable is the current scope of declared Identifiers */
  void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck);

  /* Method to generate assembly Instructions and store them in the internalState.
   * Calls the appropriate function in the internalState's CodeGenVisitor */
  void generateAssembly(InternalState internalState);

  /* All nodes must have a getter and a setter for their current SymbolTable */
  void setCurrSymTable(SymbolTable currSymTable);

  SymbolTable getCurrSymTable();
}