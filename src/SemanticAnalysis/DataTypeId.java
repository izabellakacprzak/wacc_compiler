package SemanticAnalysis;

public abstract class DataTypeId extends Identifier {

  public DataTypeId() {
    super(null);
  }

  @Override
  public abstract boolean equals(Object object);
}
