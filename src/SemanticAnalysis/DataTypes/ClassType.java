package SemanticAnalysis.DataTypes;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.AttributeNode;
import SemanticAnalysis.ConstructorId;
import SemanticAnalysis.DataTypeId;

import java.util.ArrayList;
import java.util.List;

public class ClassType extends DataTypeId {

    private final List<AttributeNode> fields;
    private final List<ConstructorId> constructors;
    private final IdentifierNode node;
    private static final int HASH_PRIME = 61;

    public ClassType(IdentifierNode node, List<AttributeNode> fields) {
        this.node = node;
        this.fields = fields;
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

    public String getClassName() {
        return node.getIdentifier();
    }


    public List<AttributeNode> getFields() {
        return fields;
    }

    public List<ConstructorId> getConstructors() {
        return constructors;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClassType)) {
            return false;
        }

        return  object.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return node.hashCode() * HASH_PRIME;
    }

    @Override
    public String toString() {

        StringBuilder fieldRep = new StringBuilder();
        for (AttributeNode id : fields) {
            fieldRep.append(id.toString()).append("; ");
        }

        return "Class " + node.toString() + "{" + fieldRep.toString() + "}";
    }
}