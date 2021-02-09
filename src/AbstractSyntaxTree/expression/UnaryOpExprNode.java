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
    operand.semanticAnalysis(symbolTable, errorMessages);

    // ... with operator
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return null;
  }
}