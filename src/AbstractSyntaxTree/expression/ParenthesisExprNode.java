package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ParenthesisExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final ExpressionNode innerExpr;

  public ParenthesisExprNode(int line, int charPositionInLine, ExpressionNode innerExpr) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.innerExpr = innerExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return innerExpr.getType(symbolTable);
  }
}