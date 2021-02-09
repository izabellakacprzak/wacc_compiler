package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Operator.UnOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class UnaryOpExprNode implements ExpressionNode {

  private final ExpressionNode operand;
  private final UnOp operator;

  public UnaryOpExprNode(ExpressionNode operand, UnOp operator) //, OperatorType operator)
  {
    this.operand = operand;
    this.operator = operator;
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