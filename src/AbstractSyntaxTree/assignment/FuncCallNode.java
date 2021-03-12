package AbstractSyntaxTree.assignment;

import static SemanticAnalysis.DataTypes.BaseType.Type.CHAR;
import static SemanticAnalysis.DataTypes.BaseType.Type.STRING;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;

import java.util.ArrayList;
import java.util.List;

public class FuncCallNode extends AssignRHSNode {

  /* identifier:   IdentifierNode corresponding to the function's name identifier
   * arguments:    List of ExpressionNodes corresponding to the arguments
   *                 passed into the function call */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> arguments;

  public FuncCallNode(
      int line, int charPositionInLine, IdentifierNode identifier, List<ExpressionNode> arguments) {
    super(line, charPositionInLine);
    this.identifier = identifier;
    this.arguments = arguments;
  }

  public Identifier getIdentifier(SymbolTable symbolTable) {
    return symbolTable.lookup("*" + identifier.getIdentifier());
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
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Check that the function has been previously declared as a FunctionId with its identifier */
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());

    if (functionId == null) {
      errorMessages.add(
          super.getLine()
              + ":"
              + super.getCharPositionInLine()
              + " No declaration of '"
              + identifier.getIdentifier()
              + "' identifier."
              + " Expected: FUNCTION IDENTIFIER");
      return;
    }

    if (!(functionId instanceof FunctionId) && !(functionId instanceof OverloadFuncId)) {
      errorMessages.add(
          super.getLine()
              + ":"
              + super.getCharPositionInLine()
              + " Incompatible type of '"
              + identifier.getIdentifier()
              + "' identifier."
              + " Expected: FUNCTION IDENTIFIER"
              + " Actual: "
              + identifier.getType(symbolTable));
      return;
    }

    List<DataTypeId> argTypes = new ArrayList<>();
    for (ExpressionNode arg : arguments) {
      argTypes.add(arg.getType(symbolTable));
    }

    /* Check that function has been called with the correct number of arguments */
    FunctionId function;
    if (functionId instanceof OverloadFuncId) {
      function = ((OverloadFuncId) functionId).findFunc(argTypes);
      if (function == null) {
        errorMessages.add(
            super.getLine()
                + ":"
                + super.getCharPositionInLine()
                + " Function call'"
                + functionId.toString()
                + "' does not match any of the Overload signatures for function '"
                + identifier.getIdentifier()
                + "'");
        return;
      }
    } else {
      function = (FunctionId) functionId;
    }

    List<DataTypeId> paramTypes = function.getParamTypes();

    if (paramTypes.size() > arguments.size() || paramTypes.size() < arguments.size()) {
      errorMessages.add(
          super.getLine()
              + ":"
              + super.getCharPositionInLine()
              + " Function '"
              + identifier.getIdentifier()
              + "' has been called with the incorrect number of parameters."
              + " Expected: "
              + paramTypes.size()
              + " Actual: "
              + arguments.size());
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
        errorMessages.add(
            super.getLine()
                + ":"
                + super.getCharPositionInLine()
                + " Could not resolve type of parameter "
                + (i + 1)
                + " in '"
                + identifier
                + "' function."
                + " Expected: "
                + currParamType);
      } else if (!(currArg.equals(currParamType)) && !stringToCharArray(currParamType, currArg)) {
        errorMessages.add(
            super.getLine()
                + ":"
                + super.getCharPositionInLine()
                + " Invalid type for parameter "
                + (i + 1)
                + " in '"
                + identifier
                + "' function."
                + " Expected: "
                + currParamType
                + " Actual: "
                + currArg);
      }
      arguments.get(i).semanticAnalysis(symbolTable, errorMessages);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState
        .getCodeGenVisitor()
        .visitFuncCallNode(internalState, identifier, arguments, getCurrSymTable());
  }

  /* Return the return type of the function
   * TODO: CHANGE FOR OVERLOAD */
  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());
    FunctionId function = (FunctionId) functionId;

    if (function == null) {
      return null;
    }

    return function.getType();
  }

  public List<DataTypeId> getOverloadType(SymbolTable symbolTable) {
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());
    List<DataTypeId> returnTypes;

    List<DataTypeId> argTypes = new ArrayList<>();
    for (ExpressionNode arg : arguments) {
      argTypes.add(arg.getType(symbolTable));
    }
    returnTypes = ((OverloadFuncId) functionId).findReturnTypes(argTypes);

    return returnTypes;
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
