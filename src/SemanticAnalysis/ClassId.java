package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.AttributeNode;

import java.util.ArrayList;
import java.util.List;

public class ClassId extends Identifier{

    private final List<AttributeNode> fields;
    private final List<ConstructorId> constructors;

    public ClassId(IdentifierNode node, List<AttributeNode> fields) {
        super(node);
        this.fields = fields;
        constructors = new ArrayList<>();
    }

    public void addConstructor(ConstructorId constructor) {
        constructors.add(constructor);
    }


    public List<ConstructorId> getConstructors() {
        return constructors;
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

        StringBuilder fieldRep = new StringBuilder();
        for (AttributeNode id : fields) {
            fieldRep.append(id.toString()).append("; ");
        }

        return "Class " + super.getNode().toString() + "{" + fieldRep.toString() + "}";
    }
}
