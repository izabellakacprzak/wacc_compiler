package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ArrayTypeNode implements TypeNode {

  /* type:  TypeNode of the corresponding type of elements the array contains */
  private final TypeNode type;

  public ArrayTypeNode(TypeNode type) {
    this.type = type;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on type */
    type.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return new ArrayType((DataTypeId) type.getIdentifier(symbolTable));
  }

  @Override
  public DataTypeId getType() {
    return new ArrayType(type.getType());
  }
}