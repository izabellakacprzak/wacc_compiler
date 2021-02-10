package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Operator.UnOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;
import java.util.Set;

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
    Set<DataTypeId> argTypes = operator.getArgTypes();

    if (!argTypes.isEmpty() && !argTypes.contains(opType)) {
      DataTypeId expected = argTypes.stream().findFirst().get();

      errorMessages.add(line + ":" + charPositionInLine
          + " Invalid type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + expected.toString().toUpperCase()
          + " Actual: " + opType.toString().toUpperCase());
    } else if (!(opType instanceof ArrayType)) {
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
}