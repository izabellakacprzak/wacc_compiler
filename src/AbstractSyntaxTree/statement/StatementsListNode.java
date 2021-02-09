package AbstractSyntaxTree.statement;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public class StatementsListNode implements StatementNode {

  private final List<StatementNode> statements;

  public StatementsListNode(List<StatementNode> statements) {
    this.statements = statements;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    for (StatementNode stat : statements) {
      stat.semanticAnalysis(symbolTable, errorMessages);
    }
  }
}