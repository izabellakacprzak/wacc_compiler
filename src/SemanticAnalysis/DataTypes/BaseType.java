package SemanticAnalysis.DataTypes;

import SemanticAnalysis.DataTypeId;

public class BaseType extends DataTypeId {

  /* type: BaseType.Type enum corresponding to the base type this represents */
  private final Type type;

  public BaseType(Type type) {
    this.type = type;
  }

  public Type getBaseType() {
    return type;
  }

  /* BaseTypes are equal if their type is the same */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BaseType)) {
      return false;
    }

    BaseType baseType = (BaseType) o;
    return baseType.getBaseType() == type;
  }

  @Override
  public String toString() {
    return type.toString();
  }

  public enum Type {INT, BOOL, CHAR, STRING}
}
