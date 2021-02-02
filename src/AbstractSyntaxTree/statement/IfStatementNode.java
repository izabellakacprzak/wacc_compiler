public class IfStatementNode extends StatementNode {
    private ExpressionNode condition;
    private StatementNode thenStatement;
    private StatementNode elseStatement;

    public IfStatementNode(ExpressionNode condition, StatementNode thenStatement,
        StatementNode elseStatement) {
            this.condition = condition;
            this.thenStatement = thenStatement;
            this.elseStatement = elseStatement;
    }
}