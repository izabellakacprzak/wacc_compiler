package AbstractSyntaxTree.type;

public class ArrayTypeNode extends TypeNode {
    private TypeNode type;

    public ArrayTypeNode(TypeNode type) {
        this.type = type;
    }
}