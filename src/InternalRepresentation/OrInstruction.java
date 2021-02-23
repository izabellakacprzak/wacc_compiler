package InternalRepresentation;

public class OrInstruction implements Instruction{

    private final Register destReg;
    private final Register operand1;
    private final Register operand2;

    public OrInstruction(Register dest, Register operand1, Register operand2) {
        this.destReg = dest;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return "OR " + destReg.getRegName() + ", " +
                operand1.getRegName() + ", " +
                operand2.getRegName();
    }
}
