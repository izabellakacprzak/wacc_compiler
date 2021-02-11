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
          + " No declaration of '" + identifier.getIdentifier() + "' identifier."
          + " Expected: FUNCTION IDENTIFIER");
      return;
    }

    if (!(functionId instanceof FunctionId)) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Incompatible type of '" + identifier.getIdentifier() + "' identifier."
          + " Expected: FUNCTION IDENTIFIER"
          + " Actual: " + identifier.getType(symbolTable) + " IDENTIFIER");
      return;
    }

    FunctionId function = (FunctionId) functionId;
    List<DataTypeId> paramTypes = function.getParamTypes();

    if (paramTypes.size() > arguments.size() || paramTypes.size() < arguments.size()) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Function '" + identifier.getIdentifier()
          + "' has been called with the incorrect number of parameters."
          + " Expected: " + paramTypes.size() + " Actual: " + arguments.size());
      return;
    }

    for (int i = 0; i < arguments.size(); i++) {
      DataTypeId currArg = arguments.get(i).getType(symbolTable);
      DataTypeId currParamType = paramTypes.get(i);

      if (currParamType == null) {
        break;
      }

      if (currArg == null) {
        errorMessages.add(line + ":" + charPositionInLine
            + " Could not resolve type of parameter " + i + " in '" + identifier + "' function."
            + " Expected: " + currParamType);
      } else if (!(currArg.equals(currParamType))) {
        errorMessages.add(line + ":" + charPositionInLine
            + " Invalid type for parameter " + i + " in '" + identifier + "' function."
            + " Expected: " + currParamType + " Actual: " + currArg);
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