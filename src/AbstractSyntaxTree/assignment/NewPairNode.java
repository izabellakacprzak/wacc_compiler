package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;

public class NewPairNode implements AssignRHSNode {
    private ExpressionNode leftExpr;
    private ExpressionNode rightExpr;

    public NewPairNode(ExpressionNode leftExpr, ExpressionNode rightExpr) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }
}