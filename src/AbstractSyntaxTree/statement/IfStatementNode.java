package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class IfStatementNode extends StatementNode {

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

    if (conditionType == null) {
      errorMessages.add(condition.getLine() + ":" + condition.getCharPositionInLine()
          + " Could not resolve type for '" + condition + "'."
          + " Expected: BOOL");
    } else if (!conditionType.equals(new BaseType(BaseType.Type.BOOL))) {
      errorMessages.add(condition.getLine() + ":" + condition.getCharPositionInLine()
          + " Incompatible type for 'If' condition."
          + " Expected: BOOL Actual: " + conditionType);
    }

    thenStatement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
    elseStatement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
  }

  @Override
  public boolean hasReturnStatement() {
    return (thenStatement.hasReturnStatement() || thenStatement.hasExitStatement())
        && (elseStatement.hasExitStatement() || elseStatement.hasReturnStatement());
  }

  @Override
  public boolean hasExitStatement() {
    return this.hasReturnStatement();
  }

  @Override
  public void setReturnType(DataTypeId returnType) {
    thenStatement.setReturnType(returnType);
    elseStatement.setReturnType(returnType);
  }
}