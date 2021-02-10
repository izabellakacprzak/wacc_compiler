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

    ExpressionNode fstExpr = expressions.get(0);
    DataTypeId fstType = fstExpr.getType(symbolTable);

    for (int i = 1; i < expressions.size(); i++) {
      ExpressionNode currExpr = expressions.get(i);
      DataTypeId currType = currExpr.getType(symbolTable);

      if (!(fstType.equals(currType))) {
        errorMessages.add(line + ":" + charPositionInLine
            + " Multiple element types in array literal. Expected type: " +
            fstType.toString() + ". Given type: " + currType.toString());
        break;
      }
    }

    for (ExpressionNode expr : expressions) {
      expr.semanticAnalysis(symbolTable, errorMessages);
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
      return expressions.get(0).getType(symbolTable);
    }
  }
}