package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.Identifier;
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
        // expr is placeholder for identifier
        DataTypeId type = expr.getType(symbolTable);
        if (!(expr instanceof Identifier)) {
            errorMessages.add(expr.toString() + "is not an instance of a Pair");
        }

    }

    @Override
    public DataTypeId getType(SymbolTable symbolTable) {
        return expr.getType(symbolTable);
    }
}

