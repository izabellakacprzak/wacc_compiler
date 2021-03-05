package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;

public final class MovInstruction implements Instruction {

  /* Default condition code */
  private final static ConditionCode DEFAULT_COND = null;
  /* Default source register */
  private final static Register DEFAULT_SRC_REG = null;
  /* Default integer immediate value */
  private final static Integer DEFAULT_INT_IMM = null;
  /* Default char immediate value */
  private final static char DEFAULT_CHAR_IMM = '\u0000';

  /* printer:       instruction printer used for generating String representations of instructions
   * destReg:       destination register where the result should be stored
   * conditionCode: condition codes for the move instruction
   * srcReg:        source register for move from register
   * intImmediate:  integer immediate value for move of immediate value
   * charImmediate: char immediate value for move of immediate value
   */
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

  /* CONDITION INSTRUCTION + SOURCE REGISTER */
  public MovInstruction(ConditionCode conditionCode, Register destReg, Register srcReg) {
    this(destReg, conditionCode, srcReg, DEFAULT_INT_IMM, DEFAULT_CHAR_IMM);
  }

  /* CONDITION INSTRUCTION + INTEGER IMMEDIATE VALUE */
  public MovInstruction(ConditionCode conditionCode, Register destReg, int intImmediate) {
    this(destReg, conditionCode, DEFAULT_SRC_REG, intImmediate, DEFAULT_CHAR_IMM);
  }

  /* CONDITION INSTRUCTION + CHAR IMMEDIATE VALUE */
  public MovInstruction(ConditionCode conditionCode, Register destReg, char charImmediate) {
    this(destReg, conditionCode, DEFAULT_SRC_REG, DEFAULT_INT_IMM, charImmediate);
  }

  /* SIMPLE INSTRUCTION + SOURCE REGISTER */
  public MovInstruction(Register destReg, Register srcReg) {
    this(DEFAULT_COND, destReg, srcReg);
  }

  /* SIMPLE INSTRUCTION + INTEGER IMMEDIATE VALUE */
  public MovInstruction(Register destReg, int intImmediate) {
    this(DEFAULT_COND, destReg, intImmediate);
  }

  /* SIMPLE INSTRUCTION + CHAR IMMEDIATE VALUE */
  public MovInstruction(Register destReg, char charImmediate) {
    this(DEFAULT_COND, destReg, charImmediate);
  }

  /* Generates string representation of ARM instruction */
  public String writeInstruction() {
    return printer.printMOV(conditionCode, destReg, srcReg, intImmediate, charImmediate);
  }

}
