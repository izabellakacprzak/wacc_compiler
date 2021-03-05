package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;

public final class MovInstruction implements Instruction {

  private final static ConditionCode DEFAULT_COND = null;
  private final static Register DEFAULT_SRC_REG = null;
  private final static Integer DEFAULT_INT_IMM = null;
  private final static char DEFAULT_CHAR_IMM = '\u0000';

  private final InstructionPrinter printer = new InstructionPrinter();
  private final Register destReg;
  private final ConditionCode conditionCode;
  private final Register srcReg;
  private final Integer intImmediate;
  private final char charImmediate;

  private MovInstruction(Register destReg, ConditionCode conditionCode, Register srcReg,
      Integer intImmediate, char charImmediate) {
    this.destReg = destReg;
    this.conditionCode = conditionCode;
    this.srcReg = srcReg;
    this.intImmediate = intImmediate;
    this.charImmediate = charImmediate;
  }

  //COND INSTR + DEST REG + SRC REG
  public MovInstruction(ConditionCode conditionCode, Register destReg, Register srcReg) {
    this(destReg, conditionCode, srcReg, DEFAULT_INT_IMM, DEFAULT_CHAR_IMM);
  }

  //COND INSTR + DEST REG + INT
  public MovInstruction(ConditionCode conditionCode, Register destReg, int intImmediate) {
    this(destReg, conditionCode, DEFAULT_SRC_REG, intImmediate, DEFAULT_CHAR_IMM);
  }

  //COND INSTR + DEST REG + CHAR
  public MovInstruction(ConditionCode conditionCode, Register destReg, char charImmediate) {
    this(destReg, conditionCode, DEFAULT_SRC_REG, DEFAULT_INT_IMM, charImmediate);
  }

  //SIMPLE INSTR + DEST REG + SRC REG
  public MovInstruction(Register destReg, Register srcReg) {
    this(DEFAULT_COND, destReg, srcReg);
  }

  //SIMPLE INSTR + DEST REG + INT
  public MovInstruction(Register destReg, int intImmediate) {
    this(DEFAULT_COND, destReg, intImmediate);
  }

  //SIMPLE INSTR + DEST REG + CHAR
  public MovInstruction(Register destReg, char charImmediate) {
    this(DEFAULT_COND, destReg, charImmediate);
  }

  public String writeInstruction() {
    return printer.printMOV(conditionCode, destReg, srcReg, intImmediate, charImmediate);
  }

}
