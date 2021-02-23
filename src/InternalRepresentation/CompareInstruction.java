package InternalRepresentation;

public class CompareInstruction implements Instruction{

    private final Register operand1;
    private final Register operand2;
    // ASR / imm val compare?

    public CompareInstruction(Register operand1, Register operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return "CMP " + operand1.getRegName() + ", " + operand2.getRegName();
    }
}