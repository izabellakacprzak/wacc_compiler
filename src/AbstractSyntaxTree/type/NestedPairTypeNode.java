package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.NestedPairType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class NestedPairTypeNode implements TypeNode{

  @Override
  public Identifier getIdentifier(SymbolTable parentSymbolTable) {
    return null;
  }

  @Override
  public DataTypeId getType() {
    return new NestedPairType();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public String toString() {
    return "pair";
  }
}
