package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ArrayLiterNode implements AssignRHSNode {

    private final List<ExpressionNode> expressions;

    public ArrayLiterNode(List<ExpressionNode> expressions) {
        this.expressions = expressions;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }
}