package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.Register;
import InternalRepresentation.InstructionPrinter;

public final class SMullInstruction implements Instruction {

    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register destReg1;
    private final Register destReg2;
    private final Register operand1;
    private final Register operand2;
    private final boolean setBits;  // TODO: USE SET BITS IN PRINTER

    public SMullInstruction(Register dest1, Register dest2, Register operand1,
        Register operand2, boolean setBits) {
        this.destReg1 = dest1;
        this.destReg2 = dest2;

        this.operand1 = operand1;
        this.operand2 = operand2;
        this.setBits = setBits;
    }

    @Override
    public String writeInstruction() {
        return printer.printSMull(setBits, destReg1, destReg2, operand1, operand2);
    }
}
