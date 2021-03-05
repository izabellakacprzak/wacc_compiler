package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Utils.Operand;

public final class CompareInstruction implements Instruction {

    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register operand1;
    private final Operand operand2;

    public CompareInstruction(Register operand1, Operand operand2) {

        assert operand1 != null;
        assert operand2 != null;

        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return printer.printCompare(operand1, operand2);
    }
}