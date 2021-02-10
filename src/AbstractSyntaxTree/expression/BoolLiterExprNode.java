package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BoolLiterExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final boolean value;

  public BoolLiterExprNode(int line, int charPositionInLine, boolean value) {
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
    return new BaseType(BaseType.Type.BOOL);
  }
}