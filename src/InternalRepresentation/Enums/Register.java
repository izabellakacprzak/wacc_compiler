package InternalRepresentation.Enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Register {
  R0, R1, R2, R3, R4, R5, R6,
  R7, R8, R9, R10, R11, R12,
  SP, LR, PC;

  /* Reserved destination register */
  public static Register DEST_REG = R0;

  /* Last register before registers store values from the stack */
  public static Register LAST_LOAD_REG = R10;

  /* Number of reserved registers to hold values from the stack */
  public static int NUM_STACK_REGS = 2;

  public String getRegName() {
    return name().toLowerCase(Locale.ROOT);
  }

  // TODO: Make constants for reserved parameter registers

  public static List<Register> getParamRegs() {
    List<Register> registers = new ArrayList<>(List.of(Register.values()));

    registers.remove(DEST_REG);
    registers.remove(R1);
    registers.remove(R2);
    registers.remove(R3);
    registers.remove(Register.SP);
    registers.remove(Register.LR);
    registers.remove(Register.PC);

    return registers;
  }
}