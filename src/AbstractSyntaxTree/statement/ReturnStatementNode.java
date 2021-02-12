package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReturnStatementNode extends StatementNode {

  /* returnExpr:  ExpressionNode corresponding to the expression 'return' was called with
   * returnType:  DataTypeId of the expected return type according to the function's declaration */
  private final ExpressionNode returnExpr;
  private DataTypeId returnType;

  public ReturnStatementNode(ExpressionNode returnExpr) {
    this.returnExpr = returnExpr;
  }



  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on expression node */
    returnExpr.semanticAnalysis(symbolTable, errorMessages);

    /* Check to see if the current symbolTable is the top table.
     * Return statements cannot be present in the body of the main function */
    if (symbolTable.isTopSymTable()) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
          + " 'return' statement cannot be present in the body of the main function.");
      return;
    }

    /* Check that the type of returnExpr is the same as the expected returnType */
    DataTypeId returnExprType = returnExpr.getType(symbolTable);

    if (returnExprType == null) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
          + " Could not resolve type for '" + returnExpr + "'.");
    } else if (!(returnExprType.equals(returnType)) && !charArrayToString(returnType, returnExprType)) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
          + " Declared return type does not match 'return' statement type."
          + " Expected: " + returnType + " Actual: " + returnExprType);
    }
  }

  // TODO: do we need this comment?
  /* true for this StatementNode */
  @Override
  public boolean hasReturnStatement() {
    return true;
  }

  /* Sets the expected returnType */
  @Override
  public void setReturnType(DataTypeId returnType) {
    this.returnType = returnType;
  }
}
