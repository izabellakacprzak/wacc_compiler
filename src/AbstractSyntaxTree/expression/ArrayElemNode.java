package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

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
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    identifier.semanticAnalysis(symbolTable, errorMessages);
    for (ExpressionNode expression : expressions) {
      expression.semanticAnalysis(symbolTable, errorMessages);
    }

    Identifier idType = symbolTable.lookupAll(identifier.getIdentifier());

    if (idType == null) {
      errorMessages.add(line + ":" + charPositionInLine
          + " No declaration of '" + identifier.getIdentifier() + "' identifier."
          + " Expected: ARRAY IDENTIFIER.");
      return;

    }
    if (!(identifier.getType(symbolTable) instanceof ArrayType)) {
      System.out.println(identifier);
      errorMessages.add(line + ":" + charPositionInLine
          + " Incompatible type of '" + identifier.getIdentifier() + "' identifier."
          + " Expected: ARRAY IDENTIFIER Actual: " + idType.toString() + "IDENTIFIER");
      return;
    }

    DataTypeId thisType;
    for (ExpressionNode expression : expressions) {
      thisType = expression.getType(symbolTable);

      if (thisType == null) {
        errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
            + " Could not resolve type of '" + expression + "' in ARRAY ELEM."
            + " Expected: INT");
        break;
      }

      if (!thisType.equals(new BaseType(BaseType.Type.INT))) {
        errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
            + " Incompatible type of '" + expression + "' in ARRAY ELEM."
            + " Expected: INT Actual: " + thisType);
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
    DataTypeId idType = identifier.getType(symbolTable);
    if(!(idType instanceof ArrayType)) {
      return null;
    }

    return ((ArrayType) idType).getElemType();
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
