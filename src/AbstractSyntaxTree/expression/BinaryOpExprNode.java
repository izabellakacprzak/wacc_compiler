package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BinaryOpExprNode extends ExpressionNode {

  private final ExpressionNode left;
  private final ExpressionNode right;
  private final BinOp operator;

  public BinaryOpExprNode(int line, int charPositionInLine, ExpressionNode left,
      ExpressionNode right,
      BinOp operator) {
    super(line, charPositionInLine);
    this.left = left;
    this.right = right;
    this.operator = operator;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    left.semanticAnalysis(symbolTable, errorMessages);
    right.semanticAnalysis(symbolTable, errorMessages);

    List<DataTypeId> argTypes = operator.getArgTypes();

    DataTypeId lhsType = left.getType(symbolTable);
    DataTypeId rhsType = right.getType(symbolTable);

    /* LHS Expression and RHS Expression types do not match */
    if (lhsType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Could not resolve type of LHS expression for '" + operator.getLabel() + "' operator."
          + " Expected: " + listTypeToString(argTypes));
      return;
    }

    if (rhsType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Could not resolve type of RHS expression for '" + operator.getLabel() + "' operator."
          + " Expected: " + listTypeToString(argTypes));
      return;
    }

    boolean argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (lhsType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    /* LHS Expression is not a valid type for the operator */
    if (!argTypes.isEmpty() && !argMatched) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Incompatible LHS type for '" + operator.getLabel() + "' operator."
          + " Expected: " + listTypeToString(argTypes) + " Actual: " + lhsType);
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
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Incompatible RHS type for '" + operator.getLabel() + "' operator."
          + " Expected: " + listTypeToString(argTypes) + " Actual: " + rhsType);
      return;
    }

    if (!lhsType.equals(rhsType)) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " RHS type does not match LHS type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + lhsType + " Actual: " + rhsType);
    }
  }

  private String listTypeToString(List<DataTypeId> list) {
    StringBuilder argsStr = new StringBuilder().append(list);
    argsStr.deleteCharAt(argsStr.length() - 1).deleteCharAt(0);

    return argsStr.toString();
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return operator.getReturnType();
  }

  @Override
  public String toString() {
    return left + " " + operator.getLabel() + " " + right;
  }
}
