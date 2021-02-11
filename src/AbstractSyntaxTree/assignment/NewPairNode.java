package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class NewPairNode extends AssignRHSNode {

  private final ExpressionNode leftExpr;
  private final ExpressionNode rightExpr;

  public NewPairNode(int line, int charPositionInLine, ExpressionNode leftExpr,
      ExpressionNode rightExpr) {
    super(line, charPositionInLine);
    this.leftExpr = leftExpr;
    this.rightExpr = rightExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    leftExpr.semanticAnalysis(symbolTable, errorMessages);
    rightExpr.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    if (leftExpr.getType(symbolTable) == null || rightExpr.getType(symbolTable) == null) {
      return null;
    }

    return new PairType(leftExpr.getType(symbolTable), rightExpr.getType(symbolTable));
  }

  @Override
  public String toString() {
    return "newpair(" + leftExpr + ", " + rightExpr + ")";
  }
}