package InternalRepresentation;

public class CompareInstruction implements Instruction{

    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register operand1;
    private final Register operand2;
    // ASR / imm val compare?

    public CompareInstruction(Register operand1, Register operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return printer.printCompare(operand1, operand2);
    }
}