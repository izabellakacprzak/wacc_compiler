package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ParenthesisExprNode implements ExpressionNode {

  private final ExpressionNode innerExpr;

  public ParenthesisExprNode(ExpressionNode innerExpr) {
    this.innerExpr = innerExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return innerExpr.getType(symbolTable);
  }
}