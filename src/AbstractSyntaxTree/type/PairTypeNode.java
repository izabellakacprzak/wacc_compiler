public class PairTypeNode extends TypeNode {
    private TypeNode fstType;
    private TypeNode sndType;

    public PairTypeNode(TypeNode fstType, TypeNode sndType) {
        this.fstType = fstType;
        this.sndType = sndType;
    }
}