package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType.Type;
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
  public DataTypeId getType(SymbolTable symTable) {
    return (DataTypeId) symTable.lookupAll(Type.INT.toString().toLowerCase());
  }
}