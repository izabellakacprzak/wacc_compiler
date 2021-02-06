package AbstractSyntaxTree.expression;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BoolLiterExprNode implements ExpressionNode {

  private final boolean value;

  public BoolLiterExprNode(boolean value) {
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }
}