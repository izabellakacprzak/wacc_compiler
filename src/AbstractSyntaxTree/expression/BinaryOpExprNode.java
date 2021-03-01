package AbstractSyntaxTree.expression;

import static InternalRepresentation.Enums.Reg.*;
import static InternalRepresentation.Enums.Condition.*;

import InternalRepresentation.ConditionCode;
import InternalRepresentation.Enums.Condition;
import InternalRepresentation.Enums.ShiftType;
import InternalRepresentation.Instructions.*;
import InternalRepresentation.Enums.ArithmeticOperation;
import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Instructions.SystemBuiltInFunction;
import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Enums.LogicalOperation;
import InternalRepresentation.InternalState;
import InternalRepresentation.Operand;
import InternalRepresentation.Register;
import InternalRepresentation.Shift;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BinaryOpExprNode extends ExpressionNode {

  private static final int MUL_SHIFT = 31;
  private static final int TRUE = 1;
  private static final int FALSE = 0;

  /* left:     ExpressionNode corresponding to the left expression the operator was called with
   * right:    ExpressionNode corresponding to the right expression the operator was called with
   * operator: BinOp enum representing the operator corresponding to this node */
  private final ExpressionNode left;
  private final ExpressionNode right;
  private final BinOp operator;
  private SymbolTable currSymTable = null;

  public BinaryOpExprNode(int line, int charPositionInLine, ExpressionNode left,
      ExpressionNode right,
      BinOp operator) {
    super(line, charPositionInLine);
    this.left = left;
    this.right = right;
    this.operator = operator;
  }

  /* Returns the toString of a list without the square brackets "[]"
   * surrounding the elements */
  private String listTypeToString(List<DataTypeId> list) {
    StringBuilder argsStr = new StringBuilder().append(list);
    argsStr.deleteCharAt(argsStr.length() - 1).deleteCharAt(0);

    return argsStr.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on assignment nodes */
    left.semanticAnalysis(symbolTable, errorMessages);
    right.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the left assignment type and the right assignment type
     * can be resolved and match one of the operator's expected argument types */
    List<DataTypeId> argTypes = operator.getArgTypes();
    DataTypeId lhsType = left.getType(symbolTable);
    DataTypeId rhsType = right.getType(symbolTable);

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
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Incompatible LHS type for '" + operator.getLabel() + "' operator."
          + " Expected: " + listTypeToString(argTypes) + " Actual: " + lhsType);
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
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Incompatible RHS type for '" + operator.getLabel() + "' operator."
          + " Expected: " + listTypeToString(argTypes) + " Actual: " + rhsType);
      return;
    }

    /* Check that the LHS and RHS assignment types match */
    if (!lhsType.equals(rhsType)) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " RHS type does not match LHS type for '" + operator.getLabel() + "' operator. "
          + "Expected: " + lhsType + " Actual: " + rhsType);
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    left.generateAssembly(internalState);
    Register leftResult = internalState.getPrevResult();
    right.generateAssembly(internalState);
    Register rightResult = internalState.getPrevResult();

    switch (operator) {
      case MUL:
        internalState.addInstruction(
            new SMullInstruction(leftResult, rightResult, leftResult, rightResult, false));
        internalState.addInstruction(
            new CompareInstruction(
                rightResult, new Operand(leftResult, new Shift(ShiftType.ASR, MUL_SHIFT))));
        BuiltInFunction.OVERFLOW.setUsed();
        internalState.addInstruction(new BranchInstruction(new ConditionCode(Condition.NE),
            BuiltInFunction.OVERFLOW.getMessage(), BranchOperation.BL));
        break;
      case DIV:
        internalState.addInstruction(new MovInstruction(new Register(R0), leftResult));
        internalState.addInstruction(new MovInstruction(new Register(R1), rightResult));
        BuiltInFunction.DIV_ZERO.setUsed();
        internalState.addInstruction(
            new BranchInstruction(BuiltInFunction.DIV_ZERO.getMessage(), BranchOperation.BL));
        internalState.addInstruction(
            new BranchInstruction(SystemBuiltInFunction.IDIV.getMessage(), BranchOperation.BL));
        internalState.addInstruction(new MovInstruction(leftResult, new Register(R0)));
        break;
      case MOD:
        internalState.addInstruction(new MovInstruction(new Register(R0), leftResult));
        internalState.addInstruction(new MovInstruction(new Register(R1), rightResult));
        BuiltInFunction.DIV_ZERO.setUsed();
        internalState.addInstruction(
            new BranchInstruction(BuiltInFunction.DIV_ZERO.getMessage(), BranchOperation.BL));
        internalState.addInstruction(
            new BranchInstruction(SystemBuiltInFunction.IDIVMOD.getMessage(), BranchOperation.BL));
        internalState.addInstruction(new MovInstruction(leftResult, new Register(R1)));
        break;
      case PLUS:
        internalState.addInstruction(
            new ArithmeticInstruction(ArithmeticOperation.ADD,
                leftResult, leftResult, new Operand(rightResult), true));
        BuiltInFunction.OVERFLOW.setUsed();
        internalState.addInstruction(new BranchInstruction(new ConditionCode(VS),
            BuiltInFunction.OVERFLOW.getMessage(), BranchOperation.BL));
        break;
      case MINUS:
        internalState.addInstruction(
            new ArithmeticInstruction(ArithmeticOperation.SUB,
                leftResult, leftResult, new Operand(rightResult), true));
        BuiltInFunction.OVERFLOW.setUsed();
        internalState.addInstruction(new BranchInstruction(new ConditionCode(VS),
            BuiltInFunction.OVERFLOW.getMessage(), BranchOperation.BL));
        break;
      case GREATER:
        conditionAssembly(internalState, leftResult, rightResult, GT, LE);
        break;
      case GEQ:
        conditionAssembly(internalState, leftResult, rightResult, GE, LT);
        break;
      case LESS:
        conditionAssembly(internalState, leftResult, rightResult, LT, GE);
        break;
      case LEQ:
        conditionAssembly(internalState, leftResult, rightResult, LE, GT);
        break;
      case EQUAL:
        conditionAssembly(internalState, leftResult, rightResult, EQ, NE);
        break;
      case NEQ:
        conditionAssembly(internalState, leftResult, rightResult, NE, EQ);
        break;
      case AND:
        internalState.addInstruction(
            new LogicalInstruction(
                LogicalOperation.AND, leftResult, leftResult, new Operand(rightResult)));
        break;
      case OR:
        internalState.addInstruction(
            new LogicalInstruction(
                LogicalOperation.ORR, leftResult, leftResult, new Operand(rightResult)));
    }
  }

  private void conditionAssembly(InternalState internalState, Register leftResult,
      Register rightResult, Condition trueCond, Condition falseCond) {
    internalState.addInstruction(new CompareInstruction(leftResult, new Operand(rightResult)));
    internalState.addInstruction(new MovInstruction(new ConditionCode(trueCond), leftResult, TRUE));
    internalState.addInstruction(
        new MovInstruction(new ConditionCode(falseCond), leftResult, FALSE));
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
