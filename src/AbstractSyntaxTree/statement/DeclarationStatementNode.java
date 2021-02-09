package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.TypeNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;

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
        // Ensure the variable name is valid
        identifier.semanticAnalysis(symbolTable, errorMessages);

        if (symbolTable.lookupAll(identifier.getIdentifier()) != null) {
            errorMessages.add("Variable with name " + identifier.getIdentifier() +
                    " has already been declared in the same scope.");
        } else {
            symbolTable.add(identifier.getIdentifier(),
                    new VariableId(this, (DataTypeId) type.createIdentifier(symbolTable)));
        }

        DataTypeId declaredType = type.getType();
        DataTypeId assignedType = assignment.getType(symbolTable);

        if (!declaredType.equals(assignedType)) {
            errorMessages.add("Declaration to: " + identifier.getIdentifier() + "must be of type " +
                    declaredType.toString() + " not " + assignedType.toString());
        }
    }

}

