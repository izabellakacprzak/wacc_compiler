package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;

import static SemanticAnalysis.DataTypes.BaseType.Type.CHAR;
import static SemanticAnalysis.DataTypes.BaseType.Type.STRING;

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

   boolean stringToCharArray(DataTypeId declaredType, DataTypeId assignedType) {
    if (declaredType.equals(new BaseType(STRING))) {
      return assignedType.equals(new ArrayType(new BaseType(CHAR)));
    }

    return false;
  }
}
