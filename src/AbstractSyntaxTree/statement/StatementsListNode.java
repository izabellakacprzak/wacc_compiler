package AbstractSyntaxTree.statement;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class StatementsListNode extends StatementNode {

  /* statements: List of StatementNodes corresponding to program statements */
  private final List<StatementNode> statements;
  private SymbolTable currSymTable = null;

  public StatementsListNode(List<StatementNode> statements) {
    this.statements = statements;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on each stored statement node */
    for (StatementNode stat : statements) {
      stat.semanticAnalysis(symbolTable, errorMessages);
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    for (StatementNode stat : statements) {
      stat.generateAssembly(internalState);
    }
  }

  /* Recursively call setReturnType on each statement to set
   * the expected return type of a ReturnStatementNode */
  @Override
  public void setReturnType(DataTypeId returnType) {
    statements.forEach(s -> s.setReturnType(returnType));
  }

  /* Check to see if the final statement is an ExitStatementNode */
  @Override
  public boolean hasExitStatement() {
    StatementNode last = statements.get(statements.size() - 1);
    return last.hasExitStatement();
  }

  /* Check to see if the final statement is a ReturnStatementNode */
  @Override
  public boolean hasReturnStatement() {
    StatementNode last = statements.get(statements.size() - 1);
    return last.hasReturnStatement();
  }

  /* Check to see if there are any StatementNodes after a ReturnStatementNode
   * in the statements list */
  @Override
  public boolean hasNoStatementAfterReturn() {
    for (int i = 0; i < statements.size() - 1; i++) {
      if (statements.get(i).hasReturnStatement()) {
        return false;
      }
    }
    return true;
  }
}