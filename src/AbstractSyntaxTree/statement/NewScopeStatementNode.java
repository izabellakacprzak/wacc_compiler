package AbstractSyntaxTree.statement;

public class NewScopeStatementNode extends StatementNode {
    private StatementNode statement;

    public NewScopeStatementNode(StatementNode statement) {
        this.statement = statement;
    }
}