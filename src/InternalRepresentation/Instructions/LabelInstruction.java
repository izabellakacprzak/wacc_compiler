package InternalRepresentation.Instructions;

import InternalRepresentation.InstructionPrinter;

public class LabelInstruction implements Instruction {
    /* printer: instruction printer used for generating String representations of instructions
     * label:   the label string
     */
    private final InstructionPrinter printer = new InstructionPrinter();
    private final String label;

    public LabelInstruction(String label) {
        this.label = label;
    }

    /* Generates string representation of ARM instruction */
    @Override
    public String writeInstruction() {
        return printer.printLabel(label);
    }
}