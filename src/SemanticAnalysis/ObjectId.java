package SemanticAnalysis;

import AbstractSyntaxTree.expression.IdentifierNode;

public class ObjectId extends Identifier {

    /* type: DataTypeId corresponding to the type of
     *       the represented parameter */
    private final DataTypeId type;

    public ObjectId(IdentifierNode node, DataTypeId type) {
        super(node);
        this.type = type;
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