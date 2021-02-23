package InternalRepresentation;

import java.util.Locale;

public class Register {

    private final Reg currReg;

    public Register(Reg currReg){
        this.currReg = currReg;
    }

    public Reg getCurrReg() {
        return currReg;
    }

    public String getRegName() {
        return currReg.name().toLowerCase(Locale.ROOT);
    }

    public enum Reg {R0, R1, R2, R3, R4, R5 , R6,
                        R7, R8, R9, R10, R11, R12,
                        SP, LR, PC }
}