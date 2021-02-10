package AbstractSyntaxTree.statement;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class StatementsListNode extends StatementNode {
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

  @Override
  public boolean hasReturnStatement() {
    StatementNode last = statements.get(statements.size() - 1);
    return last.hasReturnStatement();
  }

  @Override
  public boolean hasNoStatementAfterReturn() {
    for (int i = 0; i < statements.size() - 1; i++) {
      if (statements.get(i).hasReturnStatement()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean hasExitStatement() {
    StatementNode last = statements.get(statements.size() - 1);
    return last.hasExitStatement();
  }

  @Override
  public void setReturnType(DataTypeId returnType) {
    statements.forEach(s -> s.setReturnType(returnType));
  }
}