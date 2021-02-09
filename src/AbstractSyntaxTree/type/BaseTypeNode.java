package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypes.BaseType.Type;
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

//    switch (baseType) {
//      case INT:
//        try {
//          Integer.parseInt(value);
//        } catch (NumberFormatException e) {
//          if (value.matches("(\\+ | -)?\\d+")) {
//            errorMessages.add("Invalid range of int");
//          } else {
//            errorMessages.add("Invalid number format of int");
//          }
//        }
//        break;
//      case BOOL:
//        if (!value.matches("(true | false)")) {
//          errorMessages.add("Invalid format of boolean");
//        }
//        break;
//      case CHAR:
//        if (!(value.length() == 1)) {
//          errorMessages.add("Invalid format of char");
//        }
//
//        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(value)) {
//          errorMessages.add("Assigned char is not an ASCII character");
//        }
//        break;
//      case STRING:
//        if (!StandardCharsets.US_ASCII.newEncoder().canEncode(value)) {
//          errorMessages.add("Assigned string is not all ASCII characters");
//        }
//    }

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
}