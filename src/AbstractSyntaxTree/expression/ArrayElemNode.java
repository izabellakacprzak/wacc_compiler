package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;
import java.util.List;

public class ArrayElemNode implements AssignLHSNode, ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final IdentifierNode identifier;
  private final List<ExpressionNode> expressions;

  public ArrayElemNode(int line, int charPositionInLine, IdentifierNode identifier,
      List<ExpressionNode> expressions) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.identifier = identifier;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symTable, List<String> errorMessages) {
    identifier.semanticAnalysis(symTable, errorMessages);
    for (ExpressionNode expression : expressions) {
      expression.semanticAnalysis(symTable, errorMessages);
    }

    /* Is this needed? If it's 0 then it shouldn't be an array-elem */
    /*
    if (expressions.size() < 1) {
      errorMessages.add("");
    }
    */

    Identifier idType = symTable.lookupAll(identifier.getIdentifier());

    if (idType == null) {
      errorMessages.add(line + ":" + charPositionInLine
          + " No declaration of " + identifier.getIdentifier());
      return;

    } else if (!(idType instanceof ArrayType)) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Incorrect declaration of " + identifier.getIdentifier()
          + ". Expected: ARRAY. ACTUAL: " + idType.toString().toUpperCase());
      return;
    }

    DataTypeId thisType;
    for (ExpressionNode expression : expressions) {
      thisType = expression.getType(symTable);

      if (thisType == null || !thisType.equals(new BaseType(BaseType.Type.INT))) {
        String typeStr;

        if (thisType == null) {
          typeStr = "UNDEFINED";
        } else {
          typeStr = thisType.toString().toUpperCase();
        }

        errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
            + " Expected: INT Actual: " + typeStr);
      }
    }

  }

//        if (thisType == null) {
//          errorMessages.add("Type of " + identifier.getIdentifier() + "[" + i
//              + "] not found. Expected: " + arrayType.toString());
//        } else if (arrayType != thisType) {
//          errorMessages.add("Incompatible type of " + identifier.getIdentifier() + "[" + i
//              + "]. Expected: " + arrayType.toString() + " Actual: " + thisType.toString());
//        }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    VariableId arrayName = (VariableId) symbolTable.lookupAll(identifier.getIdentifier());

    return null;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();

    str.append(identifier.getIdentifier()).append("[");

    for (ExpressionNode expression : expressions) {
      str.append(expression.toString()).append("][");
    }

    str.delete(str.length() - 2, str.length() - 1);
    str.append(']');

    return str.toString();
  }
}
