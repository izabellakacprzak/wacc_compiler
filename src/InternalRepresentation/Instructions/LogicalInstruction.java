package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Utils.Operand;

public final class LogicalInstruction implements Instruction{

    /* printer:     instruction printer used for generating String representations of instructions
     * operation:   type of logical operation
     * destReg:     destination register where the result should be stored
     * operand1:    left operand of the logical operation
     * operand2:    right operand of the logical operation
     */
    private final InstructionPrinter printer = new InstructionPrinter();
    private final LogicalOperation operation;
    private final Register destReg;
    private final Register operand1;
    private final Operand operand2;

    public LogicalInstruction(LogicalOperation operation, Register destReg,
        Register operand1, Operand operand2) {

        assert operation != null;
        assert destReg != null;
        assert operand1 != null;
        assert operand2 != null;

        this.operation = operation;
        this.destReg = destReg;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /* Generates string representation of ARM instruction */
    @Override
    public String writeInstruction() {
        return printer.printLogical(operation, destReg, operand1, operand2);
    }

    /* Type of logical operation */
    public enum LogicalOperation {AND, EOR, ORR}
}