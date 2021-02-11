package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class CharLiterExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final char value;

  public CharLiterExprNode(int line, int charPositionInLine, char value) {
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
    return new BaseType(Type.CHAR);
  }

  @Override
  public String toString() {
    return Character.toString(value);
  }
}