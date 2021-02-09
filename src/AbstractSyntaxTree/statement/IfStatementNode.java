package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class IfStatementNode implements StatementNode {

    private final ExpressionNode condition;
    private final StatementNode thenStatement;
    private final StatementNode elseStatement;

    public IfStatementNode(ExpressionNode condition, StatementNode thenStatement,
                           StatementNode elseStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
        condition.semanticAnalysis(symbolTable, errorMessages);

        // get condition type - if not bool throw error
        DataTypeId conditionType = condition.getType(symbolTable);
        if (!conditionType.equals(new BaseType(null, BaseType.Type.BOOL))) {
            errorMessages.add("If Condition must be of type BOOL and not " + conditionType.toString());
        }

        thenStatement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
        elseStatement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
    }
}