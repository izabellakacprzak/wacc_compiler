package AbstractSyntaxTree.statement;

import static SemanticAnalysis.DataTypes.BaseType.Type.CHAR;
import static SemanticAnalysis.DataTypes.BaseType.Type.STRING;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;

public abstract class StatementNode implements ASTNode {

  /* Used to recursively set the function expected return type in ReturnStatementNode */
  public void setReturnType(DataTypeId returnType) {
  }

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

  /* true for all StatementNodes except for StatementsListNode. Used for syntax error checking */
  public boolean hasNoStatementAfterReturn() {
    return true;
  }


  /* Returns true when the declared type is a STRING and assigned type is a CHAR[] */
  boolean stringToCharArray(DataTypeId declaredType, DataTypeId assignedType) {
    if (declaredType.equals(new BaseType(STRING))) {
      return assignedType.equals(new ArrayType(new BaseType(CHAR)));
    }

    return false;
  }
}
