package AbstractSyntaxTree.expression;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ArrayElemNode implements ExpressionNode {

  private final IdentifierNode identifier;
  private final List<ExpressionNode> expressions;

  public ArrayElemNode(IdentifierNode identifier, List<ExpressionNode> expressions) {
    this.identifier = identifier;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }
}