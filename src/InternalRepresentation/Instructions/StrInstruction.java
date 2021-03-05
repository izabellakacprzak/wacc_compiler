package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.Register;

import InternalRepresentation.InstructionPrinter;

public final class StrInstruction implements Instruction {

  /* Default second offset */
  private final static Register DEFAULT_OFF_2 = null;
  /* Default immediate offset */
  private final static int DEFAULT_OFF_IMM = 0;
  /* Default setting of exclamation  */
  private final static boolean DEFAULT_USE_EX = false;

  /* printer: instruction printer used for generating String representations of instructions
   * type:            type of store instruction
   * destReg:         destination register where the result should be stored
   * offsetReg1:      first register offset of memory access
   * offsetReg2:      second register offset of memory access
   * offsetIm:        immediate offset of memory access
   * useExclamation:  exclamation setting, when set first update register by offset
   *                  then access memory otherwise first access memory then update register
   *                  by offset
   */
  private final InstructionPrinter printer = new InstructionPrinter();
  private final StrType type;
  private final Register destReg;
  private final Register offsetReg1;
  private final Register offsetReg2;
  private final int offsetImm;
  private final boolean useExclamation;

  private StrInstruction(StrType type, Register destReg, Register offsetReg1, Register offsetReg2,
      int offsetImm, boolean useExclamation) {
    this.type = type;
    this.destReg = destReg;
    this.offsetReg1 = offsetReg1;
    this.offsetReg2 = offsetReg2;
    this.offsetImm = offsetImm;
    this.useExclamation = useExclamation;
  }

  /* REGISTER + REGISTER */
  public StrInstruction(StrType type, Register destReg, Register offsetReg1, Register offsetReg2) {
    this(type, destReg, offsetReg1, offsetReg2, DEFAULT_OFF_IMM, DEFAULT_USE_EX);
  }

  /* REGISTER + IMMEDIATE OFFSET + EXCLAMATION */
  public StrInstruction(StrType type, Register destReg, Register offsetReg1, int offsetImm,
      boolean setExclamation) {
    this(type, destReg, offsetReg1, DEFAULT_OFF_2, offsetImm, setExclamation);
  }

  /* REGISTER + IMMEDIATE OFFSET */
  public StrInstruction(StrType type, Register destReg, Register offsetReg1, int offsetImm) {
    this(type, destReg, offsetReg1, offsetImm, DEFAULT_USE_EX);
  }

  /* REGISTER */
  public StrInstruction(StrType type, Register destReg, Register offsetReg1) {
    this(type, destReg, offsetReg1, DEFAULT_OFF_IMM);
  }

  /* Generates string representation of ARM instruction */
  @Override
  public String writeInstruction() {
    return printer.printStr(type, destReg, offsetReg1, offsetReg2, offsetImm, useExclamation);
  }

  /* Type of store instruction */
  public enum StrType {STR, STRB}
}