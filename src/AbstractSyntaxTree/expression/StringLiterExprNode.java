public class StringLiterExprNode extends ExpressionNode {
    private final String value;

    public StringLiterExprNode(String value){
      this.value = value.substring(1, value.length - 1);
    }
}