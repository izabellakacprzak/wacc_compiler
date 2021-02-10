package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class WhileStatementNode implements StatementNode {

  private final ExpressionNode condition;
  private final StatementNode statement;

  public WhileStatementNode(ExpressionNode condition, StatementNode statement) {
    this.condition = condition;
    this.statement = statement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    condition.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId conditionType = condition.getType(symbolTable);
    if (!conditionType.equals(new BaseType(BaseType.Type.BOOL))) {
      errorMessages.add(condition.getLine() + ":" + condition.getCharPositionInLine()
          + " While Condition must be of type BOOL and not " + conditionType.toString());
    }
    statement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
  }
}