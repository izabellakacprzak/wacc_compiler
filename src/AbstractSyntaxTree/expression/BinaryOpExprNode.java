package AbstractSyntaxTree.expression;

import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class BinaryOpExprNode extends ExpressionNode {

  private static final DataTypeId DEFAULT_TYPE = new BaseType(INT);

  /* left:     ExpressionNode corresponding to the left expression the operator was called with
   * right:    ExpressionNode corresponding to the right expression the operator was called with
   * operator: BinOp enum representing the operator corresponding to this node */
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

  private void matchTypes(SymbolTable symbolTable, ExpressionNode left,
      ExpressionNode right) {

    DataTypeId rhsType = right.getType(symbolTable);

    if (!(left.isUnsetParamId(symbolTable))) {
      return;
    }

    ParameterId paramLeft = left.getParamId(symbolTable);

    if (rhsType != null) {
      paramLeft.setType(rhsType);
      return;
    }

    ParameterId paramRight = right.getParamId(symbolTable);

    if (!(right.isUnsetParamId(symbolTable))) {
      paramLeft.addToMatchingParams(paramRight);

      for (DataTypeId type : operator.getArgTypes()) {
        paramLeft.addToExpectedTypes(type);
      }
    }
  }

  /* Returns the toString of a list without the square brackets "[]"
   * surrounding the elements */
  private String listTypeToString(List<DataTypeId> list) {
    StringBuilder argsStr = new StringBuilder().append(list);
    argsStr.deleteCharAt(argsStr.length() - 1).deleteCharAt(0);

    return argsStr.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Check that the left assignment type and the right assignment type
     * can be resolved and match one of the operator's expected argument types */
    List<DataTypeId> argTypes = operator.getArgTypes();
    DataTypeId lhsType = left.getType(symbolTable);
    DataTypeId rhsType = right.getType(symbolTable);

    boolean isUnsetParamLeft = left.isUnsetParamId(symbolTable);
    boolean isUnsetParamRight = right.isUnsetParamId(symbolTable);
    ParameterId leftParam = left.getParamId(symbolTable);
    ParameterId rightParam = right.getParamId(symbolTable);

    if (isUnsetParamLeft && isUnsetParamRight) {
      if (argTypes.size() == 1) {
        leftParam.setType(argTypes.get(0));
        rightParam.setType(argTypes.get(0));

      } else if (firstCheck) {
        uncheckedNodes.add(this);
        leftParam.addToMatchingParams(rightParam);
        rightParam.addToMatchingParams(leftParam);

        for (DataTypeId type : argTypes) {
          leftParam.addToExpectedTypes(type);
          rightParam.addToExpectedTypes(type);
        }

        return;

      } else if (argTypes.isEmpty()) {
        leftParam.setType(DEFAULT_TYPE);
        rightParam.setType(DEFAULT_TYPE);
        firstCheck = true;

      } else {
        leftParam.setType(argTypes.get(0));
        rightParam.setType(argTypes.get(0));
        firstCheck = true;
      }
    }

    if (isUnsetParamLeft) {
      leftParam.setType(rhsType);
    }

    if (isUnsetParamRight) {
      rightParam.setType(lhsType);
    }

    lhsType = left.getType(symbolTable);
    rhsType = right.getType(symbolTable);

    /* Recursively call semanticAnalysis on assignment nodes */
    left.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    right.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    if (lhsType == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Could not resolve type of LHS expression for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes)));
      return;
    }

    if (rhsType == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Could not resolve type of RHS expression for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes)));
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the LHS assignment's type */
    boolean argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (lhsType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    /* No expected argument types in argTypes implies any type is expected */
    if (!argTypes.isEmpty() && !argMatched) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Incompatible LHS type for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes) + " Actual: " + lhsType));
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the RHS assignment's type */
    argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (rhsType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    /* No expected argument types in argTypes implies any type is expected */
    if (!argTypes.isEmpty() && !argMatched) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Incompatible RHS type for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes) + " Actual: " + rhsType));
      return;
    }

    /* Check that the LHS and RHS assignment types match */
    if (!lhsType.equals(rhsType)) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "RHS type does not match LHS type for '" + operator.getLabel() + "' operator. "
              + "Expected: " + lhsType + " Actual: " + rhsType));
    }
  }


  private void setParamType(boolean unsetParameter, DataTypeId type, ParameterId param) {
    if (type != null || param == null) {
      return;
    }

    param.setType(operator.getArgTypes().get(0));
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitBinaryOpExprNode(internalState, left, right, operator);
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return operator.getReturnType();
  }

  /* Returns a BinaryOpExpr in the form: left_expr operator right_expr */
  @Override
  public String toString() {
    return left + " " + operator.getLabel() + " " + right;
  }
}
