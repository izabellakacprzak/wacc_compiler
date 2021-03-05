package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;

public final class PopInstruction implements Instruction {

    /* printer: instruction printer used for generating String representations of instructions
     * reg:     register to pop from stack
     */
    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register reg;

    public PopInstruction(Register reg) {
        this.reg = reg;
    }

    /* Generates string representation of ARM instruction */
    @Override
    public String writeInstruction() {
        return printer.printPop(reg);
    }
}