package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;

public final class LdrInstruction implements Instruction {

  private final static ConditionCode DEFAULT_COND = null;
  private final static String DEFAULT_CONSTANT = null;
  private final static Register DEFAULT_SRC_REG = null;
  private final static int DEFAULT_OFFSET = 0;

  private final InstructionPrinter printer = new InstructionPrinter();

  /* Always set */
  private final LdrType type;
  private final Register destReg;

  /* Not always set */
  private final ConditionCode conditionCode;
  private final String constant;
  private final Register srcReg;
  private final int immOffset;

  private LdrInstruction(LdrType type, Register destReg, ConditionCode conditionCode,
      String constant, Register srcReg, int immOffset) {

    assert type!= null;
    assert destReg != null;

    this.type = type;
    this.destReg = destReg;
    this.conditionCode = conditionCode;
    this.srcReg = srcReg;
    this.immOffset = immOffset;
    this.constant = constant;
  }

  //COND INSTR + DST REG + STRING CONSTANT
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg,
      MsgInstruction message) {
    this(type, destReg, conditionCode, message.toString(), DEFAULT_SRC_REG, DEFAULT_OFFSET);
  }

  //COND INSTR + DST REG + INT CONSTANT
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg, int constant) {
    this(type, destReg, conditionCode, Integer.toString(constant), DEFAULT_SRC_REG, DEFAULT_OFFSET);
  }

  //SIMPLE INSTR + DST REG + STRING CONSTANT
  public LdrInstruction(LdrType type, Register destReg, MsgInstruction message) {
    this(type, DEFAULT_COND, destReg, message);
  }

  //SIMPLE INSTR + DST REG + INT CONSTANT
  public LdrInstruction(LdrType type, Register destReg, int constant) {
    this(type, DEFAULT_COND, destReg, constant);
  }

  //COND INSTR + DST REG + SRC REG + OFFSET
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg,
      Register srcReg, int immOffset) {
    this(type, destReg, conditionCode, DEFAULT_CONSTANT, srcReg, immOffset);
  }

  //COND INSTR + DST REG + SRC REG
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg,
      Register srcReg) {
    this(type, conditionCode, destReg, srcReg, DEFAULT_OFFSET);
  }

  //SIMPLE INSTR + DST REG + SRC REG + OFFSET
  public LdrInstruction(LdrType type, Register destReg, Register srcReg, int immOffset) {
    this(type, DEFAULT_COND, destReg, srcReg, immOffset);
  }

  //SIMPLE INSTR + DST REG + SRC REG
  public LdrInstruction(LdrType type, Register destReg, Register srcReg) {
    this(type, destReg, srcReg, DEFAULT_OFFSET);
  }

  @Override
  public String writeInstruction() {
    return printer.printLDR(destReg,
        conditionCode,
        immOffset,
        srcReg,
        constant,
        type);
  }

  public enum LdrType {LDR, LDRSB}
}
