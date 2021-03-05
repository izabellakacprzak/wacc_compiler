package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.Register;

import InternalRepresentation.InstructionPrinter;

public final class StrInstruction implements Instruction {

  private final static Register DEFAULT_OFF_2 = null;
  private final static int DEFAULT_OFF_IMM = 0;
  private final static boolean DEFAULT_USE_EX = false;

  private final InstructionPrinter printer = new InstructionPrinter();
  private final StrType type;
  private final Register destReg;
  private final Register offsetReg1;
  private final Register offsetReg2;
  private final int offsetImm;
  private final boolean useExclamation;

  private StrInstruction(StrType type, Register destReg, Register offsetReg1, Register offsetReg2,
      int offsetImm, boolean useExclamation) {

    assert destReg != null;
    assert offsetReg1 != null;

    this.type = type;
    this.destReg = destReg;
    this.offsetReg1 = offsetReg1;
    this.offsetReg2 = offsetReg2;
    this.offsetImm = offsetImm;
    this.useExclamation = useExclamation;
  }

  public StrInstruction(StrType type, Register destReg, Register offsetReg1, Register offsetReg2) {
    this(type, destReg, offsetReg1, offsetReg2, DEFAULT_OFF_IMM, DEFAULT_USE_EX);
  }

  public StrInstruction(StrType type, Register destReg, Register offsetReg1, int offsetImm,
      boolean setExclamation) {
    this(type, destReg, offsetReg1, DEFAULT_OFF_2, offsetImm, setExclamation);
  }

  public StrInstruction(StrType type, Register destReg, Register offsetReg1, int offsetImm) {
    this(type, destReg, offsetReg1, offsetImm, DEFAULT_USE_EX);
  }

  public StrInstruction(StrType type, Register destReg, Register offsetReg1) {
    this(type, destReg, offsetReg1, DEFAULT_OFF_IMM);
  }

  @Override
  public String writeInstruction() {
    return printer.printStr(type, destReg, offsetReg1, offsetReg2, offsetImm, useExclamation);
  }

  // TODO: ADD WORD AND HALFWORD NUMS TO TYPES
  public enum StrType {STR, STRH, STRB}
}