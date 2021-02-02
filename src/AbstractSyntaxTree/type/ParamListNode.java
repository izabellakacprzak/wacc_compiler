public class ParamListNode extends TypeNode {
    private List<IdentifierNode> identifiers;
    private List<TypeNode> types;

    public ParamListNode(List<IdentifierNode> identifiers, List<TypeNode> types) {
        this.identifiers = identifiers;
        this.types = types;
    }
}