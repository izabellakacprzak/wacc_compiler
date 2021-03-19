package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

public class ObjectId extends Identifier {

    /* type:        DataTypeId corresponding to the type of the represented parameter
     * constructor: ConstructorId of the constructor used for creating this object */
    private final DataTypeId type;
    private final ConstructorId constructor;

    public ObjectId(IdentifierNode node, DataTypeId type, ConstructorId constructor) {
        super(node);
        this.type = type;
        this.constructor = constructor;
    }

    public String getName() {
        return super.getNode().getIdentifier();
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