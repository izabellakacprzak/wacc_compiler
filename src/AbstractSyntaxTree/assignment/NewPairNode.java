package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class NewPairNode implements AssignRHSNode {

  private final int line;
  private final int charPositionInLine;

  private final ExpressionNode leftExpr;
  private final ExpressionNode rightExpr;

  public NewPairNode(int line, int charPositionInLine, ExpressionNode leftExpr,
      ExpressionNode rightExpr) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.leftExpr = leftExpr;
    this.rightExpr = rightExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    leftExpr.semanticAnalysis(symbolTable, errorMessages);
    rightExpr.semanticAnalysis(symbolTable, errorMessages);
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
    return new PairType(leftExpr.getType(symbolTable), rightExpr.getType(symbolTable));
  }
}