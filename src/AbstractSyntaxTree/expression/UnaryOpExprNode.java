package AbstractSyntaxTree.expression;

import InternalRepresentation.Enums.ConditionCode;
import InternalRepresentation.Enums.LdrType;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.ArithmeticInstruction;
import InternalRepresentation.Enums.ArithmeticOperation;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.Instructions.LogicalInstruction;
import InternalRepresentation.Enums.LogicalOperation;
import InternalRepresentation.InternalState;
import InternalRepresentation.Operand;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Operator.UnOp;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class UnaryOpExprNode extends ExpressionNode {

  /* operand:  ExpressionNode corresponding to the expression the operator was called with
   * operator: UnOp enum representing the operator corresponding to this node */
  private final ExpressionNode operand;
  private final UnOp operator;
  private SymbolTable currSymTable = null;

  public UnaryOpExprNode(int line, int charPositionInLine, ExpressionNode operand, UnOp operator) {
    super(line, charPositionInLine);
    this.operand = operand;
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
    /* Recursively call semanticAnalysis on operand node */
    operand.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the operand type can be resolved and matches with one of the
     * operator's expected argument types */
    List<DataTypeId> argTypes = operator.getArgTypes();
    DataTypeId opType = operand.getType(symbolTable);

    if (opType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Could not resolve type for '" + operator.getLabel() + "' operand."
          + " Expected: " + listTypeToString(argTypes));
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the operand's type */
    boolean argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (opType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    if (!argTypes.isEmpty() && !argMatched) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Invalid type for '" + operator.getLabel() + "' operator."
          + " Expected: " + listTypeToString(argTypes) + " Actual: " + opType);

    } else if (argTypes.isEmpty() && !(opType instanceof ArrayType)) {
      /* No expected argument types in argTypes implies an ARRAY is expected */
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Incompatible type for '" + operator.getLabel() + "' operator."
          + " Expected: ARRAY Actual: " + opType);
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    operand.generateAssembly(internalState);
    Register operandResult = internalState.getPrevResult();

    switch(operator) {
      case NOT:
        internalState.addInstruction(new LogicalInstruction(LogicalOperation.EOR,
            operandResult, operandResult, new Operand(1)));
        break;
      case NEGATION:
        internalState.addInstruction(new ArithmeticInstruction(ArithmeticOperation.RSB,
            operandResult, operandResult, new Operand(0), true));
        BuiltInFunction.OVERFLOW.setUsed();
        internalState.addInstruction(new BranchInstruction(
            ConditionCode.VS,
            BuiltInFunction.OVERFLOW.getMessage(), BranchOperation.BL));
        break;
      case LEN:
        internalState.addInstruction(new LdrInstruction(LdrType.LDR, operandResult, operandResult));
        break;
      case ORD:
      case CHR:
    }
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return operator.getReturnType();
  }

  /* Returns a UnaryOpExpr in the form: operator expr */
  @Override
  public String toString() {
    return operator.getLabel() + " " + operand;
  }
}