package SemanticAnalysis;

public abstract class DataTypeId extends Identifier{

  /* Base types have no node associated with them */
  public DataTypeId() {
    super(null);
  }

  /* A DataTypeId's type is themself */
  @Override
  public DataTypeId getType() {
    return this;
  }

  @Override
  public int getSize() {
    return 0;
  }

  /* All DataTypeIds must Override the equals method */
  @Override
  public abstract boolean equals(Object object);

  @Override
  public abstract int hashCode();
}
