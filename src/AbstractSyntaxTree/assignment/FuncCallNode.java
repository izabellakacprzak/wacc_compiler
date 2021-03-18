package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import InternalRepresentation.Utils.StandardFunc;
import SemanticAnalysis.*;

import java.util.List;
import SemanticAnalysis.DataTypes.BaseType;

import static SemanticAnalysis.DataTypes.BaseType.Type.*;

public class FuncCallNode extends CallNode {

  private static final DataTypeId DEFAULT_TYPE = new BaseType(INT);
  /* identifier:   IdentifierNode corresponding to the function's name identifier
   * arguments:    List of ExpressionNodes corresponding to the arguments
   *                 passed into the function call */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> arguments;
  private boolean isStdFunction = false;
  private DataTypeId returnType = null;

  public FuncCallNode(
      int line, int charPositionInLine, IdentifierNode identifier, List<ExpressionNode> arguments) {
    super(line, charPositionInLine);
    this.identifier = identifier;
    this.arguments = arguments;
  }

  public Identifier getIdentifier(SymbolTable symbolTable) {
    return symbolTable.lookup("*" + identifier.getIdentifier());
  }

  public DataTypeId getReturnType() {
    return returnType;
  }

  public void setReturnType(DataTypeId type) {
    returnType = type;
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

    /* Check to see if the function called is a standard function */
    StandardFunc stdFunc = StandardFunc.valueOfLabel(identifier.getIdentifier());

    if (functionId == null && stdFunc != null) {
      returnType = stdFunc.getReturnType();
      isStdFunction = true;
      stdFunc.setUsed();
    }

    if (functionId == null && stdFunc == null) {
      errorMessages.add(new SemanticError(
          super.getLine(), super.getCharPositionInLine(),
          "No declaration of '" + identifier.getIdentifier() + "' identifier."
              + " Expected: FUNCTION IDENTIFIER"));
      return;
    }

    if (!(functionId instanceof FunctionId) && !(functionId instanceof OverloadFuncId)
        && stdFunc == null) {
      errorMessages.add(new SemanticError(
          super.getLine(), super.getCharPositionInLine(),
          "Incompatible type of '" + identifier.getIdentifier() + "' identifier."
              + " Expected: FUNCTION IDENTIFIER Actual: " + identifier.getType(symbolTable)));
      return;
    }

    super.semAnalyseFunctionArgs(symbolTable, errorMessages, identifier, arguments, functionId,
        uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor()
        .visitFuncCallNode(internalState, identifier, arguments, isStdFunction, returnType,
            getCurrSymTable());
  }

  /* Return the return type of the function */
  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());
    FunctionId function = (FunctionId) functionId;

    if (function == null) {
      StandardFunc stdFunc = StandardFunc.valueOfLabel(identifier.getIdentifier());

      if (stdFunc == null) {
        return null;
      }

      return stdFunc.getReturnType();
    }

    return function.getType();
  }

  public List<DataTypeId> getOverloadType(SymbolTable symbolTable) {
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());
    return getOverloadDataTypeIds(symbolTable, (OverloadFuncId) functionId, arguments);
  }

  /* Returns a FuncCall in the form: call func_id(arg1, arg2, ..., argN) */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("call ").append(identifier.getIdentifier()).append('(');

    return getString(str, arguments);
  }
}
