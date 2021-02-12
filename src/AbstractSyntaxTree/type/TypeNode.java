package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

public interface TypeNode extends ASTNode {

  /* Returns an Identifier corresponding to the type or identifier type
   * associated with the TypeNode */
  Identifier getIdentifier(SymbolTable symbolTable);

  /* Returns the DataTypeId corresponding to the TypeNode's DataType or return DataType */
  DataTypeId getType();
}