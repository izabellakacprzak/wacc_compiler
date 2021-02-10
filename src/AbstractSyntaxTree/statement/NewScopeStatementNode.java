package AbstractSyntaxTree.statement;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public class NewScopeStatementNode implements StatementNode {

  private final StatementNode statement;

  public NewScopeStatementNode(StatementNode statement) {
    this.statement = statement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    statement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
  }
}