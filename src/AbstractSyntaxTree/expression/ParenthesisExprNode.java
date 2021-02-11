package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ParenthesisExprNode extends ExpressionNode {

  private final ExpressionNode innerExpr;

  public ParenthesisExprNode(int line, int charPositionInLine, ExpressionNode innerExpr) {
    super(line, charPositionInLine);
    this.innerExpr = innerExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    innerExpr.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return innerExpr.getType(symbolTable);
  }

  @Override
  public String toString() {
    return "(" + innerExpr + ")";
  }
}