package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ArrayLiterNode extends AssignRHSNode {

  /* expressions: List of ExpressionNodes corresponding to each element of the ARRAY literal */
  private final List<ExpressionNode> expressions;

  public ArrayLiterNode(int line, int charPositionInLine, List<ExpressionNode> expressions) {
    super(line, charPositionInLine);
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* If there are no expressions, then ARRAY literal is empty */
    if (expressions.isEmpty()) {
      return;
    }

    /* Check if the first expression's type can be resolved */
    ExpressionNode fstExpr = expressions.get(0);
    DataTypeId fstType = fstExpr.getType(symbolTable);

    if (fstType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Could not resolve type of array assignment.");
      return;
    }

    /* Check if the other elements' types can be resolved and match the first element's type */
    for (int i = 1; i < expressions.size(); i++) {
      ExpressionNode currExpr = expressions.get(i);
      DataTypeId currType = currExpr.getType(symbolTable);

      if (currType == null) {
        errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
            + " Could not resolve element type(s) in array literal."
            + " Expected: " + fstType);
        break;
      }

      if (!(fstType.equals(currType))) {
        errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
            + " Incompatible element type(s) in array literal."
            + " Expected: " + fstType + " Actual: " + currType);
        break;
      }

      /* Recursively call semanticAnalysis on each expression node */
      currExpr.semanticAnalysis(symbolTable, errorMessages);
    }

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    if (expressions.size() == 0) {
      return new ArrayType(null);
    } else {
      return new ArrayType(expressions.get(0).getType(symbolTable));
    }
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();

    str.append("[");

    for (ExpressionNode expression : expressions) {
      str.append(expression.toString()).append(", ");
    }

    if (!expressions.isEmpty()) {
      str.delete(str.length() - 2, str.length() - 1);
    }

    str.append(']');

    return str.toString();
  }
}