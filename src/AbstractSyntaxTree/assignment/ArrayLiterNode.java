package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import java.util.List;

public class ArrayLiterNode implements AssignRHSNode {
    private List<ExpressionNode> expressions;

    public ArrayLiterNode(List<ExpressionNode> expressions) {
        this.expressions = expressions;
    }
}