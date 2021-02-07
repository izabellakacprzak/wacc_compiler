package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;

public class WhileStatementNode extends StatementNode {
    private final ExpressionNode condition;
    private final StatementNode statement;

    public WhileStatementNode(ExpressionNode condition, StatementNode statement) {
        this.condition = condition;
        this.statement = statement;
    }
    public boolean hasReturnStatement() {
        return statement.hasReturnStatement();
    }
}