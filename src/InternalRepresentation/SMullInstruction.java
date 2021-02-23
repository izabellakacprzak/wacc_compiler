package InternalRepresentation;

public class SMullInstruction implements Instruction {

    private final Register dest1;
    private final Register dest2;
    private final Register operand1;
    private final Register operand2;


    public SMullInstruction(Register dest1, Register dest2, Register operand1, Register operand2) {
        this.dest1 = dest1;
        this.dest2 = dest2;

        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return "SMULL " + dest1.getRegName() + ", " +
                dest2.getRegName() + ", " +
                operand1.getRegName() + ", " +
                operand2.getRegName();
    }
}
