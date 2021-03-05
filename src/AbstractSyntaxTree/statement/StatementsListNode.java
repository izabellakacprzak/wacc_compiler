package AbstractSyntaxTree.statement;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class StatementsListNode extends StatementNode {

  /* statements: List of StatementNodes corresponding to program statements */
  private final List<StatementNode> statements;

  public StatementsListNode(List<StatementNode> statements) {
    this.statements = statements;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on each stored statement node */
    for (StatementNode stat : statements) {
      stat.semanticAnalysis(symbolTable, errorMessages);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
            visitStatementsListNode(internalState,statements);
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