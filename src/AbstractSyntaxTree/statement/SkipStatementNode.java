package AbstractSyntaxTree.statement;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public class SkipStatementNode extends StatementNode {

  public String toString() {
    return "skip";
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
  }
}
