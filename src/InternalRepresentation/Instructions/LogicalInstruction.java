package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.LogicalOperation;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Operand;

public class LogicalInstruction implements Instruction{

    private final InstructionPrinter printer = new InstructionPrinter();
    private final LogicalOperation operation;
    private final Register destReg;
    private final Register operand1;
    private final Operand operand2;

    public LogicalInstruction(LogicalOperation operation, Register destReg,
        Register operand1, Operand operand2) {
        this.operation = operation;
        this.destReg = destReg;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return printer.printLogical(operation, destReg, operand1, operand2);
    }

}