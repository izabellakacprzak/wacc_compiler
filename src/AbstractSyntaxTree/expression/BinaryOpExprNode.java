package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BinaryOpExprNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final ExpressionNode lhs;
  private final ExpressionNode rhs;
  private final BinOp operator;

  public BinaryOpExprNode(int line, int charPositionInLine, ExpressionNode lhs, ExpressionNode rhs,
      BinOp operator) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.lhs = lhs;
    this.rhs = rhs;
    this.operator = operator;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    lhs.semanticAnalysis(symbolTable, errorMessages);
    rhs.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId lhsType = lhs.getType(symbolTable);
    DataTypeId rhsType = rhs.getType(symbolTable);

    /* LHS Expression and RHS Expression types do not match */
    if (lhsType == null) {
      errorMessages.add(line + ":" + charPositionInLine
              + " Could not resolve left hand side of '" + operator.getLabel() + "' operator");
      return;
    } else if (rhsType == null){
      errorMessages.add(line + ":" + charPositionInLine
          + " Could not resolve right hand side of '" + operator.getLabel() + "' operator");
      return;
    }
    if (!lhsType.equals(rhsType)) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Non-matching types for '" + operator.getLabel() + "' operator. "
          + "Expected: " + lhsType.toString()
          + " Actual: " + rhsType.toString());
    }

    List<DataTypeId> argTypes = operator.getArgTypes();
    boolean argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (lhsType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    /* LHS Expression is not a valid type for the operator */
    if (!argTypes.isEmpty() && !argMatched) {
      DataTypeId expected = argTypes.get(0);

      errorMessages.add(line + ":" + charPositionInLine
          + " Invalid types for '" + operator.getLabel() + "' operator. "
          + "Expected: " + expected.toString()
          + " Actual: " + lhsType.toString());
      return;
    }

    argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (rhsType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    /* RHS Expression is not a valid type for the operator */
    if (!argTypes.isEmpty() && !argMatched) {
      DataTypeId expected = argTypes.get(0);

      errorMessages.add(line + ":" + charPositionInLine
          + " Invalid RHS type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + expected.toString()
          + " Actual: " + rhsType.toString());
      // TODO: return;
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
    return lhs + " " + operator.getLabel() + " " + rhs;
  }
}
