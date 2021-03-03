package InternalRepresentation;

import InternalRepresentation.Enums.Register;

public class Operand {

    private int immedVal;
    private Register reg;
    private Shift shift;

    public Operand(int immedVal) {
        this.immedVal = immedVal;
    }

    public Operand(Register reg) {
        this.reg = reg;
    }

    public Operand(Register reg, Shift shift) {
        this.reg = reg;
        this.shift = shift;
    }

    @Override
    public String toString() {
        if (reg != null && shift != null) {
            return reg.getRegName() + ", " + shift.toString();
        } else if (reg != null) {
            return reg.getRegName();
        } else {
            return "#" + immedVal;
        }
    }
}
