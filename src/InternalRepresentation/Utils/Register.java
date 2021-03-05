package InternalRepresentation.Utils;

import java.util.ArrayList;
import java.util.List;

public enum Register {
  R0, R1, R2, R3, R4, R5, R6,
  R7, R8, R9, R10, R11, R12,
  SP, LR, PC;

  /* Reserved destination register */
  public static Register DEST_REG = R0;

  /* Registers reserved for system built in arguments */
  public static Register ARG_REG_1 = R1;
  public static Register ARG_REG_2 = R2;
  public static Register ARG_REG_3 = R3;

  /* Last register before registers store values from the stack */
  public static Register LAST_LOAD_REG = R10;

  /* Number of reserved registers to hold values from the stack */
  public static int NUM_STACK_REGS = 2;

  /* Returns all non-reserved registers as a list */
  public static List<Register> getParamRegs() {
    List<Register> registers = new ArrayList<>(List.of(Register.values()));

    registers.remove(DEST_REG);
    registers.remove(ARG_REG_1);
    registers.remove(ARG_REG_2);
    registers.remove(ARG_REG_3);
    registers.remove(Register.SP);
    registers.remove(Register.LR);
    registers.remove(Register.PC);

    return registers;
  }

  public String getRegName() {
    return name().toLowerCase();
  }
}