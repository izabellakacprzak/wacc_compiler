package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public interface ExpressionNode extends AssignRHSNode {

  int getLine();

  int getCharPositionInLine();

  DataTypeId getType(SymbolTable symbolTable);
}