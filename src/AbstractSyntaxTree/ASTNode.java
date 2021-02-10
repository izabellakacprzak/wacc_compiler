package AbstractSyntaxTree;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public interface ASTNode {

  void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages);
}