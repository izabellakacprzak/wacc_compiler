package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PairElemNode implements AssignRHSNode {

    private final int position; // 0 if FST otherwise SND
    private final ExpressionNode expr;

    public PairElemNode(int position, ExpressionNode expr) {
        this.position = position;
        this.expr = expr;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }

    @Override
    public DataTypeId getType(SymbolTable symbolTable) {
        // TODO: We need the Identifier? To get pair and then check its type
        return null;
    }
}