package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class IntLiterExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final int value;

  public IntLiterExprNode(int line, int charPositionInLine, int value) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.value = value;
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
    return new BaseType(BaseType.Type.INT);
  }
}