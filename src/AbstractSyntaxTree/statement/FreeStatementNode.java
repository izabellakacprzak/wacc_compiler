package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FreeStatementNode implements StatementNode {

  private final ExpressionNode expr;

  public FreeStatementNode(ExpressionNode expr) {
    this.expr = expr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }
}
