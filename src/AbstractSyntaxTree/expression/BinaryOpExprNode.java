public class BinaryOpExprNode extends ExpressionNode {
  private final ExpressionNode lhs;
  private final ExpressionNode rhs;
  //private final OperatorType operator; 

  public BinaryOpExprNode(ExpressionNode lhs, ExpressionNode rhs) //, OperatorType operator)
  {
    this.lhs = lhs;
    this.rhs = rhs;
    // this.operator = operator;
  }
}