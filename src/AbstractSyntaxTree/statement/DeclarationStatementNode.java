package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.TypeNode;

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