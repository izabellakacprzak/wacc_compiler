package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
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
        leftExpr.semanticAnalysis(symbolTable, errorMessages);
        rightExpr.semanticAnalysis(symbolTable, errorMessages);
    }

    @Override
    public DataTypeId getType(SymbolTable symbolTable) {
        return new PairType(null, leftExpr.getType(symbolTable), rightExpr.getType(symbolTable));
    }
}