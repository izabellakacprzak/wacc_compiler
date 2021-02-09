package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;

public class ReturnStatementNode extends StatementNode {
  private final ExpressionNode returnExpr;
  private DataTypeId returnType;

  public ReturnStatementNode(ExpressionNode returnExpr) {
    this.returnExpr = returnExpr;
  }

  @Override
  public boolean hasReturnStatement() {
    return true;
  }

  @Override
  public void setReturnType(DataTypeId returnType) {
    this.returnType = returnType;
  }
}