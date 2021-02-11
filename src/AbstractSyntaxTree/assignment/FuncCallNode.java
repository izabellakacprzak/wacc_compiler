package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FuncCallNode implements AssignRHSNode {

  private final int line;
  private final int charPositionInLine;

  private final IdentifierNode identifier;
  private final List<ExpressionNode> arguments;

  public FuncCallNode(int line, int charPositionInLine, IdentifierNode identifier,
      List<ExpressionNode> arguments) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.identifier = identifier;
    this.arguments = arguments;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    Identifier functionId = symbolTable.lookupAll("*" + identifier.getIdentifier());

    if (functionId == null) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Function " + identifier.getIdentifier() + " has not been declared.");
        return;
    }

    if (!(functionId instanceof FunctionId)) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Attempt at calling " + identifier.getIdentifier() + " as a function.");
      return;
    }

    FunctionId function = (FunctionId) functionId;
    List<DataTypeId> paramTypes = function.getParamTypes();

    // TODO: Print out number of expected arguments?
    if (paramTypes.size() > arguments.size()) {
      errorMessages.add(line + ":" + charPositionInLine
              + " Function " + identifier.getIdentifier() + " has been called with less arguments than expected.");
    } else if (paramTypes.size() < arguments.size()) {
      errorMessages.add(line + ":" + charPositionInLine
              + " Function " + identifier.getIdentifier() + " has been called with more arguments than expected.");
    } else {
      for (int i = 0; i < arguments.size(); i++){
       DataTypeId currArg = arguments.get(i).getType(symbolTable);
       DataTypeId currParamType = paramTypes.get(i);

       if (currArg == null || currParamType == null) {
         errorMessages.add(line + ":" + charPositionInLine
                 + " Could not resolve parameter " + i + "in function call. ");
       } else if (!(currArg.equals(currParamType))) {
         errorMessages.add(line + ":" + charPositionInLine
                 + " Invalid type for argument in function call." + "Expected: " + currParamType.toString().toUpperCase()
                 + " Actual: " + currArg.toString().toUpperCase());
       }
      }
    }
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    FunctionId function = (FunctionId) symbolTable.lookupAll("*" + identifier.getIdentifier());

    if (function == null) {
      return null;
    }

    return function.getReturnType();
  }

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