package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;
import java.util.Set;

public class BinaryOpExprNode implements ExpressionNode {

  private final ExpressionNode lhs;
  private final ExpressionNode rhs;
  private final BinOp operator;

  public BinaryOpExprNode(ExpressionNode lhs, ExpressionNode rhs, BinOp operator) {
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
    if (!lhsType.equals(rhsType)) {
      errorMessages.add("Non-matching types for '" + operator.getLabel() + "' operator. "
          + "Expected: " + lhsType.toString().toUpperCase()
          + " Actual: " + rhsType.toString().toUpperCase());

      return;
    }

    Set<DataTypeId> argTypes = operator.getArgTypes();

    /* LHS Expression is not a valid type for the operator */
    if (!argTypes.isEmpty() && !argTypes.contains(lhsType)) {
      DataTypeId expected = argTypes.stream().findFirst().get();

      errorMessages.add("Invalid LHS type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + expected.toString().toUpperCase()
          + " Actual: " + lhsType.toString().toUpperCase());
    }

    /* RHS Expression is not a valid type for the operator */
    if (!argTypes.isEmpty() && !argTypes.contains(rhsType)) {
      DataTypeId expected = argTypes.stream().findFirst().get();

      errorMessages.add("Invalid RHS type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + expected.toString().toUpperCase()
          + " Actual: " + rhsType.toString().toUpperCase());
    }

  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return operator.getReturnType();
  }
}
