package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class CharLiterExprNode implements ExpressionNode {

  private final char value;

  public CharLiterExprNode(char value) {
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(Type.CHAR);
  }
}