package AbstractSyntaxTree.expression;

public class IntLiterExprNode extends ExpressionNode {
    private final int value;

    public IntLiterExprNode(int value){
      this.value = value;
    }
}