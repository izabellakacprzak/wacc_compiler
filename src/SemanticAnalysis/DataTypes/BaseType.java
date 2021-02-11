package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class BaseType extends DataTypeId {

  private final Type type;

  public BaseType(Type type) {
    super();
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
    return type.toString();
  }

  public enum Type {INT, BOOL, CHAR, STRING}
}
