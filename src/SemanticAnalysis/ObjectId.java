package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

public class ObjectId extends Identifier {

    /* type: DataTypeId corresponding to the type of
     *       the represented parameter */
    private final DataTypeId type;
    private final ConstructorId constructor;

    public ObjectId(IdentifierNode node, DataTypeId type, ConstructorId constructor) {
        super(node);
        this.type = type;
        this.constructor = constructor;
    }

    @Override
    public DataTypeId getType() {
        return type;
    }

    @Override
    public int getSize() {
        return type.getSize();
    }

    @Override
    public String toString() {
        return type + " IDENTIFIER for '" + super.getNode() + "'";
    }
}