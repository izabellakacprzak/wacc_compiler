package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FreeStatementNode implements StatementNode {

  private final ExpressionNode expr;

  public FreeStatementNode(ExpressionNode expr) {
    this.expr = expr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    expr.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId exprType = expr.getType(symbolTable);
    // TODO: BETTER ERROR MESSAGE
    if (!exprType.equals(new PairType(null, null)) ||
        !exprType.equals(new ArrayType(null))) {
      errorMessages.add(expr.getLine() + ":" + expr.getCharPositionInLine()
          + " 'free' call can only be executed on an expression of " +
          "type Pair or Array, and not on" + exprType.toString());
    }
  }
}
