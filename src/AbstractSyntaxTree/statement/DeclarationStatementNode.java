public class DeclarationStatementNode extends StatementNode {
    private TypeNode type;
    private IdentifierNode identifier;
    private AssignRHSNode assignment;

    public DeclarationStatementNode(TypeNode type, IdentifierNode identifier, AssignRHSNode assignment) {
        this.type = type;
        this.identifier = identifier;
        this.assignment = assignment;
    }
}