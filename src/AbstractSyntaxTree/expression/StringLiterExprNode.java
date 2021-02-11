package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class StringLiterExprNode extends ExpressionNode {

  private final String value;

  public StringLiterExprNode(int line, int charPositionInLine, String value) {
    super(line, charPositionInLine);
    this.value = value.substring(1, value.length() - 1);
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(BaseType.Type.STRING);
  }

  @Override
  public String toString() {
    return value;
  }
}