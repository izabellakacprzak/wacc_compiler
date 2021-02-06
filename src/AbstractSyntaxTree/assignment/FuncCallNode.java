package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FuncCallNode implements AssignRHSNode {

    private final IdentifierNode identifier;
    private final List<ExpressionNode> arguments;

    public FuncCallNode(IdentifierNode identifier, List<ExpressionNode> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }
}