package ic.doc.semantic_analysis.data_types;

import ic.doc.semantic_analysis.DataTypeId;

public class BasicType extends DataTypeId {

  private enum Basic {INT, BOOL, CHAR}

  private final Basic type;

  public BasicType(Basic type) {
    this.type = type;
  }

  public Basic getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BasicType)) {
      return false;
    }

    BasicType basicType = (BasicType) o;
    return basicType.getType() == type;
  }

  @Override
  public String toString() {
    return type.toString().toLowerCase();
  }
}
