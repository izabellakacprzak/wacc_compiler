package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorId extends Identifier{

    private final List<ParameterId> parameters;

    public ConstructorId(IdentifierNode node, List<ParameterId> parameters) {
        super(node);
        this.parameters = parameters;
    }

    public List<DataTypeId> getSortedParameterTypes() {
        return parameters.stream().
                map(ParameterId::getType).
                sorted(Comparator.comparing(DataTypeId::hashCode)).
                collect(Collectors.toList());
    }

    public List<DataTypeId> getParameterTypes() {
        return parameters.stream().
                map(ParameterId::getType).
                collect(Collectors.toList());
    }

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
        return super.getNode().toString() + "(" + parameters.toString() + ")";
    }

    @Override
    public boolean equals (Object o) {
        if (!(o instanceof ConstructorId)) {
            return false;
        }

        ConstructorId constructor = (ConstructorId) o;
        List<DataTypeId> paramTypes = constructor.getSortedParameterTypes();

        return paramTypes.equals(getSortedParameterTypes());
    }
}
