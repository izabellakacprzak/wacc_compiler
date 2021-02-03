package ic.doc.semantic_analysis;

public class ParameterId extends Identifier {

  private final DataTypeId type;

  public ParameterId(DataTypeId type) {
    super();
    this.type = type;
  }

  @Override
  public String toString() {
    return type.toString()/* + ASTNode.identifier()*/;
  }
}
