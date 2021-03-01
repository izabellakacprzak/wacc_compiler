package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.StrType;

import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Register;

public class StrInstruction implements Instruction {

  private final InstructionPrinter printer = new InstructionPrinter();
  private final StrType type;
  private final Register destReg;
  private final Register offsetReg1;
  private Register offsetReg2;
  private int offsetImm;
  private boolean useExclamation = false;

  public StrInstruction(StrType type, Register destReg, Register offsetReg1, Register offsetReg2) {
    this.type = type;
    this.destReg = destReg;
    this.offsetReg1 = offsetReg1;
    this.offsetReg2 = offsetReg2;
  }

  public StrInstruction(StrType type, Register destReg, Register offsetReg1, int offsetImm) {
    this.type = type;
    this.destReg = destReg;
    this.offsetReg1 = offsetReg1;
    this.offsetImm = offsetImm;
  }

  public StrInstruction(StrType type, Register destReg, Register offsetReg1) {
    this.type = type;
    this.destReg = destReg;
    this.offsetReg1 = offsetReg1;
  }

  public void useExclamation() {
    useExclamation = true;

  }

  @Override
  public String writeInstruction() {
    return printer.printStr(type, destReg, offsetReg1, offsetReg2, offsetImm, useExclamation);
  }
}