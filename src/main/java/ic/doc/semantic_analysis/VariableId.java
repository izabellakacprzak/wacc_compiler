package ic.doc.semantic_analysis;

public class VariableId extends Identifier {

  private final DataTypeId type;

  public VariableId(DataTypeId type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type.toString();
  }
}