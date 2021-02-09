package AbstractSyntaxTree.expression;

import SemanticAnalysis.*;

import java.util.List;

public class IdentifierNode implements ExpressionNode {

  private final String identifier;

  public IdentifierNode(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    Identifier id = symbolTable.lookupAll(identifier);

    if(id instanceof VariableId) {
      //TODO: get type from Variable ID?
      return null;
    } else if (id instanceof FunctionId) {
      return ((FunctionId) id).getReturnType();
    } //sth else Id can be?

    return null;
  }
}
