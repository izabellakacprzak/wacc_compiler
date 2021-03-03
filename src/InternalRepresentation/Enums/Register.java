package InternalRepresentation.Enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Register {
  R0, R1, R2, R3, R4, R5, R6,
  R7, R8, R9, R10, R11, R12,
  SP, LR, PC;

  public String getRegName() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Register getDestReg() {
    return Register.R0;
  }


  public static List<Register> getParamRegs() {
    List<Register> registers = new ArrayList<>(List.of(Register.values()));

    registers.remove(getDestReg());
    registers.remove(Register.SP);
    registers.remove(Register.LR);
    registers.remove(Register.PC);

    return registers;
  }
}