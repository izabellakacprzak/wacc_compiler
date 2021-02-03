package AbstractSyntaxTree.expression;

public class CharLiterExprNode extends ExpressionNode {
    private final char value;

    public CharLiterExprNode(char value){
      this.value = value;
    }
}