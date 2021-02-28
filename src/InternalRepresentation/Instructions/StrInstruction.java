package InternalRepresentation.Instructions;

import InternalRepresentation.ConditionCode;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Register;

public class StrInstruction implements Instruction {
  private final Register destReg;
  private ConditionCode conditionCode;
  private int immOffset;
  private Register srcReg;
  private String constant = null;
  private boolean isSTRSB = false; //signed byte

  private final InstructionPrinter printer = new InstructionPrinter();

  //SIMPLE INSTR + DST REG + STRING CONSTANT
  public StrInstruction(Register destReg, String constant) {
    this.destReg = destReg;
    this.constant = constant;
  }

  //SIMPLE INSTR + DST REG + INT CONSTANT
  public StrInstruction(Register destReg, int constant) {
    this.destReg = destReg;
    this.constant = Integer.toString(constant);
  }


  //COND INSTR + DST REG + STRING CONSTANT
  public StrInstruction(ConditionCode conditionCode, Register destReg, String constant) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.constant = constant;
  }

  //COND INSTR + DST REG + INT CONSTANT
  public StrInstruction(ConditionCode conditionCode, Register destReg, int constant) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.constant = Integer.toString(constant);
  }


  //SIMPLE INSTR + DST REG + SRC REG
  public StrInstruction(Register destReg, Register srcReg) {
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = 0;
  }

  //SIMPLE INSTR + DST REG + SRC REG + OFFSET
  public StrInstruction(Register destReg, Register srcReg, int immOffset) {
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = immOffset;
  }


  //COND INSTR + DST REG + SRC REG
  public StrInstruction(ConditionCode conditionCode, Register destReg, Register srcReg) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = 0;
  }

  //COND INSTR + DST REG + SRC REG + OFFSET
  public StrInstruction(ConditionCode conditionCode, Register destReg, Register srcReg, int immOffset) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = immOffset;
  }

  public void setIsSTRSB() {
    isSTRSB = true;
  }

  @Override
  public String writeInstruction() {
    return printer.printLDR(destReg,
        conditionCode,
        immOffset,
        srcReg,
        constant,
        isSTRSB);
  }
}

