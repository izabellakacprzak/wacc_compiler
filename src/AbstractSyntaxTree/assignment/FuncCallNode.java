package AbstractSyntaxTree.assignment;

import static SemanticAnalysis.DataTypes.BaseType.Type.CHAR;
import static SemanticAnalysis.DataTypes.BaseType.Type.STRING;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Enums.*;
import InternalRepresentation.Instructions.*;
import InternalRepresentation.InternalState;
import InternalRepresentation.Operand;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class FuncCallNode extends AssignRHSNode {

  /* identifier: IdentifierNode corresponding to the function's name identifier
   * arguments:  List of ExpressionNodes corresponding to the arguments
   *               passed into the function call */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> arguments;
  private SymbolTable currSymTable = null;


  public FuncCallNode(int line, int charPositionInLine, IdentifierNode identifier,
                      List<ExpressionNode> arguments) {
    super(line, charPositionInLine);
    this.identifier = identifier;
    this.arguments = arguments;
  }

  /* Returns true when the declared type is a STRING and assigned type is a CHAR[] */
  private boolean stringToCharArray(DataTypeId declaredType, DataTypeId assignedType) {
    if (declaredType.equals(new BaseType(STRING))) {
      return assignedType.equals(new ArrayType(new BaseType(CHAR)));
    }

    return false;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Check that the function has been previously declared as a FunctionId with its identifier */
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());

    if (functionId == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " No declaration of '" + identifier.getIdentifier() + "' identifier."
                            + " Expected: FUNCTION IDENTIFIER");
      return;
    }

    if (!(functionId instanceof FunctionId)) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Incompatible type of '" + identifier.getIdentifier() + "' identifier."
                            + " Expected: FUNCTION IDENTIFIER"
                            + " Actual: " + identifier.getType(symbolTable));
      return;
    }

    /* Check that function has been called with the correct number of arguments */
    FunctionId function = (FunctionId) functionId;
    List<DataTypeId> paramTypes = function.getParamTypes();

    if (paramTypes.size() > arguments.size() || paramTypes.size() < arguments.size()) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Function '" + identifier.getIdentifier()
                            + "' has been called with the incorrect number of parameters."
                            + " Expected: " + paramTypes.size() + " Actual: " + arguments.size());
      return;
    }

    /* Check that each parameter's type can be resolved and matches the
     * corresponding argument type */
    for (int i = 0; i < arguments.size(); i++) {
      DataTypeId currArg = arguments.get(i).getType(symbolTable);
      DataTypeId currParamType = paramTypes.get(i);

      if (currParamType == null) {
        break;
      }

      if (currArg == null) {
        errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                              + " Could not resolve type of parameter " + (i + 1) + " in '" + identifier
                              + "' function."
                              + " Expected: " + currParamType);
      } else if (!(currArg.equals(currParamType)) && !stringToCharArray(currParamType, currArg)) {
        errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                              + " Invalid type for parameter " + (i + 1) + " in '" + identifier + "' function."
                              + " Expected: " + currParamType + " Actual: " + currArg);
      }
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    // calculate total arguments size in argsTotalSize
    int argsTotalSize = 0;

    // TODO avoid creating a new object for the SP here, should point to the same obj in all classes!!
    // should not be created in this class at all in the first place!!
    Register spReg = Register.SP;
    // arguments are stored in decreasing order they are given in the code
    for (int i = arguments.size() - 1; i >= 0; i--) {
      // get argument, calculate size and add it to argsTotalSize
      ExpressionNode currArg = arguments.get(i);
      int argSize = currArg.getType(currSymTable).getSize();
      argsTotalSize += argSize;
      // generate assembly code for the current argument
      currArg.generateAssembly(internalState);

      //TODO do you need to store the argument stack offset in the symbol table??
      StrType strInstr = (argSize == 1) ? StrType.STR : StrType.STRB;

      //store currArg on the stack and decrease stack pointer (stack grows downwards)
      internalState.addInstruction(
          new StrInstruction(strInstr, internalState.peekFreeRegister(), spReg, -argSize));
    }

    //Branch Instruction to the callee label
    String functionLabel = "f_" + identifier.toString();
    internalState.addInstruction(new BranchInstruction(
        ConditionCode.L, functionLabel, BranchOperation.B));

    //TODO what is stack max size? 1MB? what if argsTotalSize > stack size??
    //deallocate stack from the function arguments
    internalState.addInstruction(
        new ArithmeticInstruction(ArithmeticOperation.ADD, spReg, spReg, new Operand(argsTotalSize),
            false));

    //TODO change this R0 declaration too...
    Register r0Reg = Register.R0;

    //move the result stored in R0 in the first free register
    internalState.addInstruction(new MovInstruction(internalState.peekFreeRegister(), r0Reg));
  }

  /* Return the return type of the function */
  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    FunctionId function = (FunctionId) symbolTable.lookupAll("*" + identifier.getIdentifier());

    if (function == null) {
      return null;
    }

    return function.getType();
  }

  /* Returns a FuncCall in the form: call func_id(arg1, arg2, ..., argN) */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("call ").append(identifier.getIdentifier()).append('(');

    for (ExpressionNode argument : arguments) {
      str.append(argument.toString()).append(", ");
    }

    if (!arguments.isEmpty()) {
      str.delete(str.length() - 2, str.length() - 1);
    }

    return str.append(')').toString();
  }
}