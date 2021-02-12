package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public abstract class StatementNode implements ASTNode {

  /* false for all StatementNodes except for ExitStatementNode, overridden by nodes
   * that can have exit statements in their child nodes. Used for syntax error checking */
  public boolean hasExitStatement() {
    return false;
  }

  /* false for all StatementNodes except for ReturnStatementNode, overridden by nodes
   * that can have return statements in their child nodes. Used for syntax error checking */
  public boolean hasReturnStatement() {
    return false;
  }

  /* ltrue for all StatementNodes except for StatementsListNode. Used for syntax error checking */
  public boolean hasNoStatementAfterReturn() {
    return true;
  }

  /* Used to recursively set the function expected return type in ReturnStatementNode */
  public void setReturnType(DataTypeId returnType) {
  }
}
