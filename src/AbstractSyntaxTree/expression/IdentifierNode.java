package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;
import java.util.List;

public class IdentifierNode extends ExpressionNode {

  private final String identifier;

  public IdentifierNode(int line, int charPositionInLine, String identifier) {
    super(line, charPositionInLine);
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    Identifier id = symbolTable.lookupAll(identifier);

    if (id == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Identifier '" + identifier + "' has not been declared.");
    } else if (!(id instanceof VariableId) && !(id instanceof ParameterId)) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Identifier '" + identifier + "' is referenced incorrectly."
          + " Expected: VARIABLE IDENTIFIER, PARAMETER IDENTIFIER");
    }
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    Identifier id = symbolTable.lookupAll(identifier);

    if (id instanceof VariableId) {
      return ((VariableId) id).getType();
    } else if (id instanceof ParameterId) {
      return ((ParameterId) id).getType();
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