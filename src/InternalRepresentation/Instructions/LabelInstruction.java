package InternalRepresentation.Instructions;

import InternalRepresentation.InstructionPrinter;

public class LabelInstruction implements Instruction {
    private final InstructionPrinter printer = new InstructionPrinter();
    private final String label;

    public LabelInstruction(String label) {
        assert label != null;

        this.label = label;
    }

    @Override
    public String writeInstruction() {
        return printer.printLabel(label);
    }
}