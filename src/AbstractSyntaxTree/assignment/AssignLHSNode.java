package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public interface AssignLHSNode extends ASTNode {
    DataTypeId getType(SymbolTable symbolTable);
}