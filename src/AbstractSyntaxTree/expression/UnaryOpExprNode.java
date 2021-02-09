package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class UnaryOpExprNode implements ExpressionNode {

  private final ExpressionNode operand;
  //private final OperatorType operator;

  public UnaryOpExprNode(ExpressionNode operand) //, OperatorType operator)
  {
    this.operand = operand;
    // this.operator = operator;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    //TODO: do we get operator type, do we have to make it
    return null;
  }
}