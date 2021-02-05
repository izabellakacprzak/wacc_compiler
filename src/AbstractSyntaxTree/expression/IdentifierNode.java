package AbstractSyntaxTree.expression;

import SemanticAnalysis.SymbolTable;

import java.util.List;

public class IdentifierNode extends ExpressionNode {
  private final String identifier;

  public IdentifierNode(String identifier){
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }
}
