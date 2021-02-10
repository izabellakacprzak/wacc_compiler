package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ArrayTypeNode implements TypeNode {

  private final TypeNode type;

  public ArrayTypeNode(TypeNode type) {
    this.type = type;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    type.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public Identifier getIdentifier(SymbolTable parentSymbolTable) {
    return new ArrayType((DataTypeId) type.getIdentifier(parentSymbolTable));
  }

  @Override
  public DataTypeId getType() {
    return new ArrayType(type.getType());
  }
}