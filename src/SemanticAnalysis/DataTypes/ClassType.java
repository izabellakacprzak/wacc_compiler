package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.AttributeNode;
import SemanticAnalysis.ConstructorId;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.ParameterId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassType extends DataTypeId {

    private final List<AttributeNode> attributes;
    private final List<ConstructorId> constructors;
    private final IdentifierNode node;
    private int currOffset = 0;
    private static final int ERROR_CODE = -1;
    private static final int HASH_PRIME = 61;

    public ClassType(IdentifierNode node, List<AttributeNode> fields) {
        this.node = node;
        this.attributes = fields;
        constructors = new ArrayList<>();
        this.currOffset = 0;
    }

    public boolean addConstructor(ConstructorId constructor) {
        for (ConstructorId constructorId : constructors) {
            if (constructorId.equals(constructor)) {
                return false;
            }
        }

        constructors.add(constructor);
        return true;
    }

    public int findIndex(List<ParameterId> params) {

        List<DataTypeId> paramTypes = params.stream().map(ParameterId::getType).collect(Collectors.toList());
        List<DataTypeId> constructorTypes;

        for (int i = 0; i < constructors.size(); i++) {
            constructorTypes = constructors.get(i).getParameterTypes();

            if (paramTypes.equals(constructorTypes)) {
                return i;
            }
        }

        return ERROR_CODE;
    }

    public String getClassName() {
        return node.getIdentifier();
    }


    public List<AttributeNode> getAttributes() {
        return attributes;
    }

    public List<ConstructorId> getConstructors() {
        return constructors;
    }

    public int getCurrOffset() {
        return this.currOffset;
    }

    @Override
    public int getSize() {
        int attributeSize = 0;
        for (AttributeNode attribute : attributes) {
            attributeSize += attribute.getType().getSize();
        }

        return attributeSize;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClassType)) {
            return false;
        }

        return object.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return node.hashCode() * HASH_PRIME;
    }

    @Override
    public String toString() {

        StringBuilder fieldRep = new StringBuilder();
        for (AttributeNode id : attributes) {
            fieldRep.append(id.toString()).append("; ");
        }

        return "Class " + node.toString() + "{" + fieldRep.toString() + "}";
    }
}