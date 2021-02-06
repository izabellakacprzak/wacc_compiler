package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class BaseTypeNode implements TypeNode {
  private final BaseType.Type baseType;
  private final String value;

  public BaseTypeNode(BaseType.Type baseType, String value) {
    this.baseType = baseType;
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    switch (baseType) {
      case INT:
        try {
          Integer.parseInt(value);
        } catch (NumberFormatException e) {
          if (value.matches("(\\+ | -)?\\d+")) {
            errorMessages.add("Invalid range of int on line <line>");
          } else {
            errorMessages.add("Invalid number format of int on line <line>");
          }
        }
        break;
      case BOOL:
        if (!value.matches("(true | false)")) {
          errorMessages.add("Invalid format of boolean on line <line>");
        }
        break;
      case CHAR:
        break;
      case STRING:

    }
  }

  @Override
  public Identifier createIdentifier(SymbolTable parentSymbolTable) {
    return new BaseType(this, baseType);
  }
}