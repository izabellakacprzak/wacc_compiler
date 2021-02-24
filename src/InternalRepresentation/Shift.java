package InternalRepresentation;

public class Shift {

    private final ShiftType shiftType;
    private Register reg;
    private int immedVal;

    public Shift(ShiftType shiftType, Register reg) {
        this.shiftType = shiftType;
        this.reg = reg;
    }

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

    // TODO: SHOULD WE ALSO ADD RRX (RRX doesn't take registers or immedVal)
    public enum ShiftType {LSL, LSR, ASR, ROR}
}
