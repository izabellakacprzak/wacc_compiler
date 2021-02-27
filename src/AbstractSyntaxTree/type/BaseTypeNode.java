package AbstractSyntaxTree.type;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BaseTypeNode implements TypeNode {

  /* type: BaseType.Type enum corresponding to the base type this represents */
  private final Type baseType;

  public BaseTypeNode(Type baseType) {
    this.baseType = baseType;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return symbolTable.lookupAll(baseType.toString().toLowerCase());
  }

  @Override
  public DataTypeId getType() {
    return new BaseType(baseType);
  }
}