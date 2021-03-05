package InternalRepresentation.Utils;

public class Shift {

    /* shiftType: type of shift operation
     * reg: register value of shift
     * immedVal: immediate value of shift
     */
    private final ShiftType shiftType;
    private Register reg;
    private int immedVal;

    /* REGISTER */
    public Shift(ShiftType shiftType, Register reg) {
        this.shiftType = shiftType;
        this.reg = reg;
    }

    /* IMMEDIATE VALUE */
    public Shift(ShiftType shiftType, int immedVal) {
        this.shiftType = shiftType;
        this.immedVal = immedVal;
    }

    @Override
    public String toString() {
        if (reg != null) {
            return shiftType.name() + " " + reg.getRegName();
        } else {
            return shiftType.name() + " #" + immedVal;
        }
    }

    /* Types of shift operations */
    public enum ShiftType {LSL, ASR}
}
