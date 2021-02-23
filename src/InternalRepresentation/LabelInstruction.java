package InternalRepresentation;

public class LabelInstruction implements Instruction{

    private final String label;

    public LabelInstruction(String label) {
        this.label = label;
    }

    @Override
    public String writeInstruction() {
        return label + ":";
    }
}