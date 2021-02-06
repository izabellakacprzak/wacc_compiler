package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class NewPairNode implements AssignRHSNode {

    private final ExpressionNode leftExpr;
    private final ExpressionNode rightExpr;

    public NewPairNode(ExpressionNode leftExpr, ExpressionNode rightExpr) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }
}