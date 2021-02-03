package AbstractSyntaxTree.expression;

public class IdentifierNode extends ExpressionNode {
  private final String identifier;

  public IdentifierNode(String identifier){
    this.identifier = identifier;
  }
}
