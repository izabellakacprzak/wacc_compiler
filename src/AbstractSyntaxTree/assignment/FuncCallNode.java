package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;

import java.util.List;

import static SemanticAnalysis.DataTypes.BaseType.Type.*;

public class FuncCallNode extends AssignRHSNode {

  private static final DataTypeId DEFAULT_TYPE = new BaseType(INT);
  /* identifier:   IdentifierNode corresponding to the function's name identifier
   * arguments:    List of ExpressionNodes corresponding to the arguments
   *                 passed into the function call */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> arguments;

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
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Check that the function has been previously declared as a FunctionId with its identifier */
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());

    if (functionId instanceof FunctionId) {
      FunctionId function = (FunctionId) functionId;
      List<ParameterId> params = function.getParams();


      boolean containsUnsetParam = false;
      for (int i = 0; i < params.size() && i < arguments.size(); i++) {
        ExpressionNode currArg = arguments.get(i);
        ParameterId currParam = params.get(i);
        if ((currParam.getType() == null || currParam.isUnsetArray())
                && currArg.getType(symbolTable) != null) {
          currParam.setType(currArg.getType(symbolTable));
        } else if (currParam.getType() == null || currParam.isUnsetArray()) {
          containsUnsetParam = true;
        }
      }

      if (containsUnsetParam && firstCheck) {
        uncheckedNodes.add(this);
        return;
      } else if (containsUnsetParam) {
        for (int i = 0; i < params.size() && i < arguments.size(); i++) {
          ExpressionNode currArg = arguments.get(i);
          if (currArg.isUnsetParamId(symbolTable)) {
            ParameterId currParam = currArg.getParamId(symbolTable);
            currParam.setType(DEFAULT_TYPE);
            params.get(i).setType(DEFAULT_TYPE);
            firstCheck = true;
          } else if (currArg.isUnsetParamArray(symbolTable)) {
            ParameterId currParam =
                ((ArrayElemNode) currArg).getUnsetParameterIdArrayElem(symbolTable);
            currParam.setType(DEFAULT_TYPE);
            params.get(i).setType(DEFAULT_TYPE);
            firstCheck = true;
          }
        }
      }
    }


    if (functionId == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "No declaration of '" + identifier.getIdentifier() + "' identifier."
              + " Expected: FUNCTION IDENTIFIER"));
      return;
    }

    if (!(functionId instanceof FunctionId)) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Incompatible type of '" + identifier.getIdentifier() + "' identifier."
              + " Expected: FUNCTION IDENTIFIER"
              + " Actual: " + identifier.getType(symbolTable)));
      return;
    }

    /* Check that function has been called with the correct number of arguments */
    FunctionId function = (FunctionId) functionId;
    List<DataTypeId> paramTypes = function.getParamTypes();

    if (paramTypes.size() > arguments.size() || paramTypes.size() < arguments.size()) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Function '" + identifier.getIdentifier()
              + "' has been called with the incorrect number of parameters."
              + " Expected: " + paramTypes.size() + " Actual: " + arguments.size()));
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
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Could not resolve type of parameter " + (i + 1) + " in '" + identifier
                + "' function."
                + " Expected: " + currParamType));
      } else if (!(currArg.equals(currParamType)) && !stringToCharArray(currParamType, currArg)) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Invalid type for parameter " + (i + 1) + " in '" + identifier + "' function."
                + " Expected: " + currParamType + " Actual: " + currArg));
      }
      arguments.get(i).semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
                                         visitFuncCallNode(internalState, identifier, arguments, getCurrSymTable());
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