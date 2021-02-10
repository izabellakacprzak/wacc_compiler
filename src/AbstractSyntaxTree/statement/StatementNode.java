package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;

public abstract class StatementNode implements ASTNode {

  public boolean hasReturnStatement() {
    return false;
  }

  public boolean hasNoStatementAfterReturn() {
    return true;
  }

  public boolean hasExitStatement() {
    return false;
  }

  public void setReturnType(DataTypeId returnType) {
  }
}
