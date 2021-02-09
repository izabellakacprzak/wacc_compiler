package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BinaryOpExprNode implements ExpressionNode {

  private final ExpressionNode lhs;
  private final ExpressionNode rhs;
  private final BinOp operator;

  public BinaryOpExprNode(ExpressionNode lhs, ExpressionNode rhs, BinOp operator) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.operator = operator;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    lhs.semanticAnalysis(symbolTable, errorMessages);
    rhs.semanticAnalysis(symbolTable, errorMessages);

    // ... with operator
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return null;
  }
}
