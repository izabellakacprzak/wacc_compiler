package AbstractSyntaxTree.type;

public class BaseTypeNode extends TypeNode {
  // some sort of an enum for type (INT, BOOL etc) ??
  private String value;

  public BaseTypeNode(String value) {
    this.value = value;
  }
}