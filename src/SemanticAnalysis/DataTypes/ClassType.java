package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.AttributeNode;
import SemanticAnalysis.ConstructorId;
import SemanticAnalysis.DataTypeId;

import java.util.ArrayList;
import java.util.List;

public class ClassType extends DataTypeId {

    /* ERROR_CODE: returned in findIndexConstructor when no matching constructor can be found */
    private static final int ERROR_CODE = -1;
    /* HASH_PRIME: prime number used for hashCode generation */
    private static final int HASH_PRIME = 61;

    /* attributes:      list of AttributeNodes representing the attributes of that class
     * constructors:    list of ConstructorIds of constructors of this class
     * node:            IdentifierNode representing this class */
    private final List<AttributeNode> attributes;
    private final List<ConstructorId> constructors;
    private final IdentifierNode node;


    public ClassType(IdentifierNode node, List<AttributeNode> fields) {
        this.node = node;
        this.attributes = fields;
        constructors = new ArrayList<>();
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

    public DataTypeId getAttributeType(int index) {
        return attributes.get(index).getType();
    }

    public int findIndexConstructor(List<DataTypeId> paramTypes) {

        List<DataTypeId> constructorTypes;

        for (int i = 0; i < constructors.size(); i++) {
            constructorTypes = constructors.get(i).getParameterTypes();

            if (paramTypes.equals(constructorTypes)) {
                return i;
            }
        }

        /* Could not found a matching constructor, return ERROR_CODE */
        return ERROR_CODE;
    }

    public int findIndexAttribute(String attribute) {
        for(int i = 0; i < attributes.size(); i++) {
            if(attributes.get(i).toString().equals(attribute)) {
                return i;
            }
        }
        return -1;
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