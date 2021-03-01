package InternalRepresentation;

import InternalRepresentation.Enums.Reg;

import java.util.ArrayList;
import java.util.List;
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

    public static Reg getDestReg() {
        return Reg.R0;
    }

    public static List<Reg> getParamRegs() {
        List<Reg> registers = new ArrayList<>(List.of(Reg.values()));

        registers.remove(getDestReg());
        registers.remove(Reg.SP);
        registers.remove(Reg.LR);
        registers.remove(Reg.PC);

        return registers;
    }
}