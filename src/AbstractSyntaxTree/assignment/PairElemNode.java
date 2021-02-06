package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PairElemNode implements AssignLHSNode, AssignRHSNode {

    private final int position; // 0 if FST otherwise SND
    private final ExpressionNode expr;

    public PairElemNode(int position, ExpressionNode expr) {
        this.position = position;
        this.expr = expr;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }
}