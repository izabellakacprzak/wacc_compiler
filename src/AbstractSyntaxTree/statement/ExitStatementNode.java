package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ExitStatementNode implements StatementNode {

    private final ExpressionNode expr;

    public ExitStatementNode(ExpressionNode expr) {
        this.expr = expr;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
        expr.semanticAnalysis(symbolTable, errorMessages);

        DataTypeId exprType = expr.getType(symbolTable);

        if (!exprType.equals(new BaseType(null, BaseType.Type.INT))){
            errorMessages.add("Exit code must be of type INT not" + exprType.toString());
        }
    }
}