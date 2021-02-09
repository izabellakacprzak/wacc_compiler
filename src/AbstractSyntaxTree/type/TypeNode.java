package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

public interface TypeNode extends ASTNode {
    Identifier getIdentifier(SymbolTable parentSymbolTable);
}