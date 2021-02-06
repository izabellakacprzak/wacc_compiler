package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
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

  }
}