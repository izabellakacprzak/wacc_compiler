package AbstractSyntaxTree.expression;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public class StringLiterExprNode implements ExpressionNode {

  private final String value;

  public StringLiterExprNode(String value) {
    this.value = value.substring(1, value.length() - 1);
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }
}