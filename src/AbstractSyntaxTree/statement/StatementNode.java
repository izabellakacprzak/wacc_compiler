package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public abstract class StatementNode implements ASTNode {

  /* false for all StatementNodes except for ExitStatementNode and StatementsListNode */
  public boolean hasExitStatement() {
    return false;
  }

  /* false for all StatementNodes except for ReturnStatementNode and StatementsListNode */
  public boolean hasReturnStatement() {
    return false;
  }

  /* true for all StatementNodes except for StatementsListNode */
  public boolean hasNoStatementAfterReturn() {
    return true;
  }

  /* Used to set the expected return type in ReturnStatementNode */
  public void setReturnType(DataTypeId returnType) {
  }
}
