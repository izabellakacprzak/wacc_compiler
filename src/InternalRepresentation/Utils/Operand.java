package InternalRepresentation.Utils;

public class Operand {

    /* immedVal:    immediate value operand
     * reg:         register operand
     * shift:       shift value for register operand
     */
    private int immedVal;
    private Register reg;
    private Shift shift;

    /* IMMEDIATE VALUE */
    public Operand(int immedVal) {
        this.immedVal = immedVal;
    }

    /* REGISTER */
    public Operand(Register reg) {
        this.reg = reg;
    }

    /* REGISTER + SHIFT */
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
