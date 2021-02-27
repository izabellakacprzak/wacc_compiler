package InternalRepresentation;

import InternalRepresentation.Enums.Reg;

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
}