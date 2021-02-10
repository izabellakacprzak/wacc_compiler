package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReturnStatementNode implements StatementNode {

  private final ExpressionNode returnExpr;
  private final DataTypeId returnType = new BaseType(BaseType.Type.INT);  //dummy return type

  public ReturnStatementNode(ExpressionNode returnExpr) {
    this.returnExpr = returnExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    returnExpr.semanticAnalysis(symbolTable, errorMessages);

    // if symbol table is top one - sem error - must return from a non-main func
    if (symbolTable.isTopSymTable()) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
          + " Return statement cannot be present in the body of the main function.");
    }

    // compare return type from func declaration and returnExpr.type
    DataTypeId returnExprType = returnExpr.getType(symbolTable);
    if (returnExprType == null) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
              + " Failed to procure type of return expression " + returnExpr.toString() + ".");
    } else if ((returnExprType.equals(returnType))) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
              + " Return statement does not match expected return type."
              + " Expected: " + returnType.toString().toUpperCase()
              + " Actual: " + returnExprType.toString().toUpperCase());
    }
  }
}