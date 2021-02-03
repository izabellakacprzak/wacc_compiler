package AbstractSyntaxTree.expression;

public class ParenthesisExprNode extends ExpressionNode {
    private final ExpressionNode innerExpr;

    public ParenthesisExprNode(ExpressionNode innerExpr){
      this.innerExpr = innerExpr;
    }
}