public class ExitStatementNode extends StatementNode {
    private ExpressionNode expr;

    public ExitStatementNode(ExpressionNode expr) {
        this.expr = expr;
    }
}