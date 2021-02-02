public class ReturnStatementNode extends StatementNode {
    private final ExpressionNode returnExpr;

    public final ReturnStatementNode(ExpressionNode returnExpr){
      this.returnExpr = returnExpr;
    }
}