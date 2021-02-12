package SemanticAnalysis;

public abstract class DataTypeId extends Identifier {

  /* Base types have no node associated with them */
  public DataTypeId() {
    super(null);
  }

  @Override
  public abstract boolean equals(Object object);
}
