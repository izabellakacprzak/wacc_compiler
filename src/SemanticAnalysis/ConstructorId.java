package SemanticAnalysis;

import java.util.List;

public class ConstructorId extends Identifier{

    private List<ParameterId> parameters;

    @Override
    public DataTypeId getType() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }

    // TODO: override equals for parameters so we can compare different constructors
}
