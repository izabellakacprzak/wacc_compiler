public class UnaryOpExprNode extends ExpressionNode {
    private final ExpressionNode operand;
    //private final OperatorType operator;

    public UnaryOpExprNode(ExpressionNode operand) //, OperatorType operator)
    {
      this.operand = operand;
      // this.operator = operator;
    }
}