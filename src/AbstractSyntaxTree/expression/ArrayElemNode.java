public class ArrayElemNode extends ExpressionNode {
    private final IdentfierNode identifier;
    private final List<ExpressionNode> expressions;

    public ArrayElemNode(IdentfierNode identifier, List<ExpressionNode> expressions){
      this.identifier = identifier;
      this.expressions = expressions;
    }
}