package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PairLiterExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  public PairLiterExprNode(int line, int charPositionInLine) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

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
    return new PairType(null, null);
  }
}