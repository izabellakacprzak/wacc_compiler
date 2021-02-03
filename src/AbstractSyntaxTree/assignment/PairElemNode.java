package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;

public class PairElemNode implements AssignRHSNode {
    private int position; // 0 if FST otherwise SND
    private ExpressionNode expr;

    public PairElemNode(int position, ExpressionNode expr) {
        this.position = position;
        this.expr = expr;
    }
}