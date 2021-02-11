package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Operator.UnOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class UnaryOpExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final ExpressionNode operand;
  private final UnOp operator;

  public UnaryOpExprNode(int line, int charPositionInLine, ExpressionNode operand, UnOp operator) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.operand = operand;
    this.operator = operator;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    operand.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId opType = operand.getType(symbolTable);

    if (opType == null) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Could not resolve operand '" + operator.getLabel() + "' type");
      return;
    }

    List<DataTypeId> argTypes = operator.getArgTypes();
    boolean argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (opType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    if (!argTypes.isEmpty() && !argMatched) {
      DataTypeId expected = argTypes.get(0);

      errorMessages.add(line + ":" + charPositionInLine
          + " Invalid type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + expected.toString().toUpperCase()
          + " Actual: " + opType.toString().toUpperCase());

    } else if (argTypes.isEmpty() && !(opType instanceof ArrayType)) {
      DataTypeId expected = new ArrayType(null);

      errorMessages.add(line + ":" + charPositionInLine
          + " Invalid type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + expected.toString().toUpperCase()
          + " Actual: " + opType.toString().toUpperCase());
    }
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return operator.getReturnType();
  }

  @Override
  public String toString() {
    return operator.getLabel() + " " + operand;
  }
}