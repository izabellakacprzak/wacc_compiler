package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;

public final class PushInstruction implements Instruction {
    /* printer: instruction printer used for generating String representations of instructions
     * reg:     register to push on stack
     */
    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register reg;

    public PushInstruction(Register reg) {
        this.reg = reg;
    }

    /* Generates string representation of ARM instruction */
    @Override
    public String writeInstruction() {
        return printer.printPush(reg);
    }
}