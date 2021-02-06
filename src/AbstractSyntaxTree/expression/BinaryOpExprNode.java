package AbstractSyntaxTree.expression;

import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BinaryOpExprNode implements ExpressionNode {

  private final ExpressionNode lhs;
  private final ExpressionNode rhs;
  //private final OperatorType operator; 

  public BinaryOpExprNode(ExpressionNode lhs, ExpressionNode rhs) //, OperatorType operator)
  {
    this.lhs = lhs;
    this.rhs = rhs;
    // this.operator = operator;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }
}