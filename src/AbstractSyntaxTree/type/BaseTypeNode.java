package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class BaseTypeNode implements TypeNode {
  private final BaseType.Type baseType;
  private String value;

  public BaseTypeNode(BaseType.Type baseType, String value) {
    this.baseType = baseType;
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
  }

  @Override
  public Identifier createIdentifier(SymbolTable parentSymbolTable) {
    return new BaseType(this, baseType);
  }

  @Override
  public DataTypeId getType() {
    return new BaseType(null, baseType);
  }
}