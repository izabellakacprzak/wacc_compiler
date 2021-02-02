public class ParanthesesExprNode extends ExpressionNode {
    private final ExpressionNode innerExpr;

    public ParanthesesExprNode(ExpressionNode innerExpr){
      this.innerExpr = innerExpr;
    }
}