package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class BaseType extends DataTypeId {

  public enum Type {INT, BOOL, CHAR}

  private final Type type;

  public BaseType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BaseType)) {
      return false;
    }

    BaseType baseType = (BaseType) o;
    return baseType.getType() == type;
  }

  @Override
  public String toString() {
    return type.toString().toLowerCase();
  }
}
