package InternalRepresentation;

public class AndInstruction implements Instruction{

    private final Register dest;
    private final Register operand1;
    private final Register operand2;

    public AndInstruction(Register dest, Register operand1, Register operand2) {
        this.dest = dest;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return "AND " + dest.getRegName() + ", " +
                operand1.getRegName() + ", " +
                operand2.getRegName();
    }
}
