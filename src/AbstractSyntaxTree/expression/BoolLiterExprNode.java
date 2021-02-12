package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BoolLiterExprNode extends ExpressionNode {

  /* value: boolean representing the value of this node's BOOL literal */
  private final boolean value;

  public BoolLiterExprNode(int line, int charPositionInLine, boolean value) {
    super(line, charPositionInLine);
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(BaseType.Type.BOOL);
  }

  @Override
  public String toString() {
    return Boolean.toString(value);
  }
}