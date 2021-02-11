package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ArrayLiterNode implements AssignRHSNode {

  private final int line;
  private final int charPositionInLine;

  private final List<ExpressionNode> expressions;

  public ArrayLiterNode(int line, int charPositionInLine, List<ExpressionNode> expressions) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    if (!expressions.isEmpty()) {
      ExpressionNode fstExpr = expressions.get(0);
      DataTypeId fstType = fstExpr.getType(symbolTable);
      if (fstType == null) {
        errorMessages.add(line + ":" + charPositionInLine
                + " Could not resolve type of array assignment.");
      } else {
        for (int i = 1; i < expressions.size(); i++) {
          ExpressionNode currExpr = expressions.get(i);
          DataTypeId currType = currExpr.getType(symbolTable);

          if (currType == null) {
            errorMessages.add(line + ":" + charPositionInLine
                    + " Could not resolve element type(s) in array literal."
                    + " Expected: " + fstType);
            break;
          } else if (!(fstType.equals(currType))) {
            errorMessages.add(line + ":" + charPositionInLine
                    + " Incompatible element type(s) in array literal."
                    + " Expected: " + fstType + " Actual: " + currType);
            break;

          }
          currExpr.semanticAnalysis(symbolTable, errorMessages);
        }
      }
    }
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
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