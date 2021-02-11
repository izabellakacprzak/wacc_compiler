package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;
import java.util.List;

public class IdentifierNode implements ExpressionNode {

  private final int line;
  private final int charPositionInLine;

  private final String identifier;

  public IdentifierNode(int line, int charPositionInLine, String identifier) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    Identifier id = symbolTable.lookupAll(identifier);

    if (id == null) {
      errorMessages.add(line + ":" + charPositionInLine
              + " Identifier " + identifier + " has not been declared.");
    } else if (!(id instanceof VariableId)) {
      errorMessages.add(line + ":" + charPositionInLine
              + " Identifier " + identifier + " is referenced incorrectly as a variable.");
    }

    id = symbolTable.lookupAll("*" + identifier);
    if (id instanceof FunctionId) {
      errorMessages.add(line + ":" + charPositionInLine
              + " Function " + identifier + " is referenced incorrectly as a variable.");
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
    Identifier id = symbolTable.lookupAll(identifier);

    if (id instanceof VariableId) {
      return ((VariableId) id).getType();
    }
    // check whether identifier is a function instance
    id = symbolTable.lookupAll("*" + identifier);
    if (id instanceof FunctionId) {
      return ((FunctionId) id).getReturnType();
    }

    return null;
  }

  @Override
  public String toString() {
    return identifier;
  }
}