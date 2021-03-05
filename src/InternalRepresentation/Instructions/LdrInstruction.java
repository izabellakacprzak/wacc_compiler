package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;

public final class LdrInstruction implements Instruction {

  /* Default condition code */
  private final static ConditionCode DEFAULT_COND = null;
  /* Default constant */
  private final static String DEFAULT_CONSTANT = null;
  /* Default source register */
  private final static Register DEFAULT_SRC_REG = null;
  /* Default offset */
  private final static int DEFAULT_OFFSET = 0;

  /* printer:       instruction printer used for generating String representations of instructions
   * type:          type of load instruction
   * destReg:       destination register where the value should be loaded
   * conditionCode: condition codes for the load instruction
   * constant:      constant memory address
   * srcReg:        source register memory address
   * immOffset:     offset for source register memory address
   */
  private final InstructionPrinter printer = new InstructionPrinter();
  private final LdrType type;
  private final Register destReg;
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

  /* CONDITION INSTRUCTION + STRING CONSTANT */
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg,
      MsgInstruction message) {
    this(type, destReg, conditionCode, message.toString(), DEFAULT_SRC_REG, DEFAULT_OFFSET);
  }

  /* CONDITION INSTRUCTION + INT CONSTANT */
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg, int constant) {
    this(type, destReg, conditionCode, Integer.toString(constant), DEFAULT_SRC_REG, DEFAULT_OFFSET);
  }

  /* SIMPLE INSTRUCTION + STRING CONSTANT */
  public LdrInstruction(LdrType type, Register destReg, MsgInstruction message) {
    this(type, DEFAULT_COND, destReg, message);
  }

  /* SIMPLE INSTRUCTION + INT CONSTANT */
  public LdrInstruction(LdrType type, Register destReg, int constant) {
    this(type, DEFAULT_COND, destReg, constant);
  }

  /* CONDITION INSTRUCTION + SOURCE REGISTER + OFFSET */
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg,
      Register srcReg, int immOffset) {
    this(type, destReg, conditionCode, DEFAULT_CONSTANT, srcReg, immOffset);
  }

  /* CONDITION INSTRUCTION + SOURCE REGISTER */
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg,
      Register srcReg) {
    this(type, conditionCode, destReg, srcReg, DEFAULT_OFFSET);
  }

  /* SIMPLE INSTRUCTION + SOURCE REGISTER + OFFSET */
  public LdrInstruction(LdrType type, Register destReg, Register srcReg, int immOffset) {
    this(type, DEFAULT_COND, destReg, srcReg, immOffset);
  }

  /* SIMPLE INSTRUCTION + SOURCE REGISTER */
  public LdrInstruction(LdrType type, Register destReg, Register srcReg) {
    this(type, destReg, srcReg, DEFAULT_OFFSET);
  }

  /* Generates string representation of ARM instruction */
  @Override
  public String writeInstruction() {
    return printer.printLDR(destReg,
        conditionCode,
        immOffset,
        srcReg,
        constant,
        type);
  }

  /* Type of load instruction */
  public enum LdrType {LDR, LDRSB}
}
