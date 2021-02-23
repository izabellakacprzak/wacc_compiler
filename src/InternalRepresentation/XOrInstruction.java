package InternalRepresentation;

public class XOrInstruction implements Instruction{

    private final Register destReg;
    private final Register operand1;
    private final Register operand2;


    public XOrInstruction(Register dest, Register operand1, Register operand2) {
        this.destReg = dest;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public String writeInstruction() {
        return "EOR " + destReg.getRegName() + ", " +
                operand1.getRegName() + ", " +
                operand2.getRegName();
    }

}