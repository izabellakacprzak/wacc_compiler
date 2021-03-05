package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;

public final class SMullInstruction implements Instruction {

    /* printer:     instruction printer used for generating String representations of instructions
     * destReg1:    first destination register
     * destReg2:    second destination register
     * operand1:    first operand of multiply operation
     * operand2:    second operand of multiply operation
     * setBits:     true if condition flags should be set false otherwise
     */
    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register destReg1;
    private final Register destReg2;
    private final Register operand1;
    private final Register operand2;
    private final boolean setBits;

    public SMullInstruction(Register dest1, Register dest2, Register operand1,
        Register operand2, boolean setBits) {

        assert dest1 != null;
        assert dest2 != null;
        assert operand1 != null;
        assert operand2 != null;

        this.destReg1 = dest1;
        this.destReg2 = dest2;

        this.operand1 = operand1;
        this.operand2 = operand2;
        this.setBits = setBits;
    }

    /* Generates string representation of ARM instruction */
    @Override
    public String writeInstruction() {
        return printer.printSMull(setBits, destReg1, destReg2, operand1, operand2);
    }
}
