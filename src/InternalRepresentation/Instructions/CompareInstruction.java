package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.Register;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Operand;

public final class CompareInstruction implements Instruction {

    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register operand1;
    private final Operand operand2;

    public CompareInstruction(Register operand1, Operand operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return printer.printCompare(operand1, operand2);
    }
}