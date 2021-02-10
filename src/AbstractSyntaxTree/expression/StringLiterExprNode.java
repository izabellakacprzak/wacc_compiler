package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class StringLiterExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final String value;

  public StringLiterExprNode(int line, int charPositionInLine, String value) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.value = value.substring(1, value.length() - 1);
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
    return new BaseType(BaseType.Type.STRING);
  }
}