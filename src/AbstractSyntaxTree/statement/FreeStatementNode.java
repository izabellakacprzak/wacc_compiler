package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FreeStatementNode extends StatementNode {

  private final ExpressionNode expr;

  public FreeStatementNode(ExpressionNode expr) {
    this.expr = expr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    expr.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId exprType = expr.getType(symbolTable);

    if (exprType == null) {
      errorMessages.add(expr.getLine() + ":" + expr.getCharPositionInLine()
          + " Could not resolve type for '" + expr + "'."
          + " Expected: ARRAY, PAIR");
    } else if (!exprType.equals(new PairType(null, null)) &&
        !exprType.equals(new ArrayType(null))) {
      errorMessages.add(expr.getLine() + ":" + expr.getCharPositionInLine()
          + " Incompatible type for 'free' statement." +
          " Expected: ARRAY, PAIR Actual: " + exprType);
    }
  }
}
