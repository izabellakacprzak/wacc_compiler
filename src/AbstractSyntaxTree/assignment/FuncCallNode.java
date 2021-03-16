package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;

import java.util.List;

public class FuncCallNode extends CallNode {

  /* identifier:   IdentifierNode corresponding to the function's name identifier
   * arguments:    List of ExpressionNodes corresponding to the arguments
   *                 passed into the function call */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> arguments;
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

    super.semAnalyseFunctionArgs(symbolTable, errorMessages, identifier, arguments, functionId);
  }



  @Override
  public void generateAssembly(InternalState internalState) {
    internalState
        .getCodeGenVisitor()
        .visitFuncCallNode(internalState, identifier, arguments, returnType, getCurrSymTable());
  }

  /* Return the return type of the function */
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
