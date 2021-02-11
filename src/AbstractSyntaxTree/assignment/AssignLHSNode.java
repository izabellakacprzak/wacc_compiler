package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public interface AssignLHSNode extends ASTNode {

  int getLine();

  int getCharPositionInLine();

  DataTypeId getType(SymbolTable symbolTable);

  @Override
  String toString();
}