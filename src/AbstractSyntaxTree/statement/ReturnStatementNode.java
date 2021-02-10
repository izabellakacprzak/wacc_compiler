package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReturnStatementNode implements StatementNode {

  private final ExpressionNode returnExpr;

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
    // TODO: GET RETURN TYPE FROM FUNCTION DECLARATION
    // compare return type from func declaration and returnExpr.type

  }
}