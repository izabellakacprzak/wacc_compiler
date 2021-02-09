package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BoolLiterExprNode implements ExpressionNode {

  private final boolean value;

  public BoolLiterExprNode(boolean value) {
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(null, BaseType.Type.BOOL);
  }
}