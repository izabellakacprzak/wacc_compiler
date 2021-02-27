package InternalRepresentation;

import static InternalRepresentation.Register.Reg.*;

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
        return R0;
    }

    public static List<Reg> getParamRegs() {
        List<Reg> registers = new ArrayList<>(List.of(Reg.values()));

        registers.remove(getDestReg());
        registers.remove(SP);
        registers.remove(LR);
        registers.remove(PC);

        return registers;
    }

    public enum Reg {
        R0, R1, R2, R3, R4, R5, R6,
        R7, R8, R9, R10, R11, R12,
        SP, LR, PC
    }
}