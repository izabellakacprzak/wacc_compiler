package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class IntLiterExprNode implements ExpressionNode {

  private final int value;

  public IntLiterExprNode(int value) {
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(null, BaseType.Type.INT);
  }
}