public class ArrayLiterNode implements AssignRHSNode {
    private List<ExpressionNode> expressions;

    public ArrayLiterNode(List<ExpressionNode> expressions) {
        this.expressions = expressions;
    }
}