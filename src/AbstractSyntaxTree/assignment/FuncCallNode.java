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
    Identifier functionId = symbolTable.lookupAll(identifier.getIdentifier());

    if (functionId == null) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Function " + identifier.getIdentifier() + " has not been declared.");
      // TODO: Do we check the arguments? plain return?
    }

    if (!(functionId instanceof FunctionId)) {
      errorMessages.add(line + ":" + charPositionInLine
          + " Attempt at calling " + identifier.getIdentifier() + " as a function.");
      // TODO: Do we check the arguments? plain return?
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
    FunctionId function = (FunctionId) symbolTable.lookupAll(identifier.getIdentifier());
    return function.getReturnType();
  }
}