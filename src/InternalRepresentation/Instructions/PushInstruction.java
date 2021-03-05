package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.Register;
import InternalRepresentation.InstructionPrinter;

public final class PushInstruction implements Instruction {
    // General Push instruction takes in a reglist but could not see such an example
    // in the WACC arm output
    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register reg;

    public PushInstruction(Register reg) {
        this.reg = reg;
    }

    @Override
    public String writeInstruction() {
        return printer.printPush(reg);
    }
}