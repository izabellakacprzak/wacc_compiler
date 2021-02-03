package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class ReturnStatementNode extends StatementNode {
    private final ExpressionNode returnExpr;

    public ReturnStatementNode(ExpressionNode returnExpr){
      this.returnExpr = returnExpr;
    }
}