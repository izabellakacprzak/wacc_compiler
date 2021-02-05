package AbstractSyntaxTree;

import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public interface ASTNode {

    void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages);
}