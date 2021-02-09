package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class BaseTypeNode implements TypeNode {
  private final Type baseType;
  private final String value;

  public BaseTypeNode(Type baseType, String value) {
    this.baseType = baseType;
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  /* Return pre-defined type in symbol table */
  @Override
  public Identifier getIdentifier(SymbolTable parentSymbolTable) {

//    if (!(type instanceof DataTypeId)) {
//      throw new Exception("BaseTypeNode.createIdentifier: Type " + baseType.toString()
//          + "not in symbol table");
//    }

    return parentSymbolTable.lookupAll(baseType.toString().toLowerCase());
  }

  @Override
  public DataTypeId getType() {
    return new BaseType(baseType);
  }
}