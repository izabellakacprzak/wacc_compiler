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
      //TODO: get type from Variable ID?
      return null;
    } else if (id instanceof FunctionId) {
      return ((FunctionId) id).getReturnType();
    } //sth else Id can be?

    return null;
  }
}
