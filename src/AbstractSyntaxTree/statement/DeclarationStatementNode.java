package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.TypeNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class DeclarationStatementNode implements StatementNode {

    private final TypeNode type;
    private final IdentifierNode identifier;
    private final AssignRHSNode assignment;

    public DeclarationStatementNode(TypeNode type, IdentifierNode identifier,
        AssignRHSNode assignment) {
        this.type = type;
        this.identifier = identifier;
        this.assignment = assignment;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }
}