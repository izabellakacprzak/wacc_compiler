package AbstractSyntaxTree.statement;

import static SemanticAnalysis.DataTypes.BaseType.Type.CHAR;
import static SemanticAnalysis.DataTypes.BaseType.Type.STRING;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.assignment.FuncCallNode;
import AbstractSyntaxTree.assignment.MethodCallNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.OverloadFuncId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import javax.xml.crypto.Data;
import java.util.List;

public abstract class StatementNode implements ASTNode {

  /* currSymTable: the node's symbol table of identifiers it can access */
  private SymbolTable currSymTable = null;

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

  DataTypeId getTypeOfOverloadFunc(SymbolTable symbolTable, List<SemanticError> errorMessages,
                                   DataTypeId leftType, AssignRHSNode right) {
    DataTypeId rightType = null;
    List<DataTypeId> returnTypes;

    if(right instanceof FuncCallNode
            && ((FuncCallNode) right).getIdentifier(symbolTable) instanceof OverloadFuncId) {
      returnTypes = ((FuncCallNode) right).getOverloadType(symbolTable);
      for(DataTypeId returnType : returnTypes) {
        if(returnType == null) {
          continue;
        }

        if(returnType.equals(leftType)) {
          rightType = returnType;
          break;
        }
      }

      if(rightType == null) {
        errorMessages.add(new SemanticError(right.getLine(), right.getCharPositionInLine(),
                "RHS type does not match LHS type for statement.'"
                + " Expected: " + leftType + ". Could not find matching return type"
                + " in overloaded functions."));
        return null;
      } else {
        ((FuncCallNode) right).setReturnType(rightType);
      }
    } else if (right instanceof MethodCallNode
            && ((MethodCallNode) right).getIdentifier(symbolTable) instanceof OverloadFuncId) {
      returnTypes = ((MethodCallNode) right).getOverloadType(symbolTable);
      for(DataTypeId returnType : returnTypes) {
        if(returnType == null) {
          continue;
        }

        if(returnType.equals(leftType)) {
          rightType = returnType;
          break;
        }
      }

      if(rightType == null) {
        errorMessages.add(new SemanticError(right.getLine(), right.getCharPositionInLine(),
                "RHS type does not match LHS type for statement.'"
                + " Expected: " + leftType + ". Could not find matching return type"
                + " in overloaded functions."));
        return null;
      } else {
        ((MethodCallNode) right).setReturnType(rightType);
      }
    } else {
      rightType = right.getType(symbolTable);
    }
    return rightType;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }
}
