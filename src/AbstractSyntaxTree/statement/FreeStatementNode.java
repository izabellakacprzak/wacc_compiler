public class FreeStatement extends StatementNode {
    private ExpressionNode expr;

    public FreeStatement(ExpressionNode expr) {
        this.expr = expr;
    }
}