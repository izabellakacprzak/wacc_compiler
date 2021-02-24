package InternalRepresentation.Instructions;

import InternalRepresentation.ConditionCode;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Instructions.Instruction;
import InternalRepresentation.Register;

public class LdrInstruction implements Instruction {
  private final Register destReg;
  private ConditionCode conditionCode;
  private int immOffset;
  private Register srcReg;
  private String constant = null;
  private boolean isLDRSB = false; //signed byte

  private final InstructionPrinter printer = new InstructionPrinter();

  //SIMPLE INSTR + DST REG + STRING CONSTANT
  public LdrInstruction(Register destReg, String constant) {
    this.destReg = destReg;
    this.constant = constant;
  }

  //SIMPLE INSTR + DST REG + INT CONSTANT
  public LdrInstruction(Register destReg, int constant) {
    this.destReg = destReg;
    this.constant = Integer.toString(constant);
  }


  //COND INSTR + DST REG + STRING CONSTANT
  public LdrInstruction(ConditionCode conditionCode, Register destReg, String constant) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.constant = constant;
  }

  //COND INSTR + DST REG + INT CONSTANT
  public LdrInstruction(ConditionCode conditionCode, Register destReg, int constant) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.constant = Integer.toString(constant);
  }


  //SIMPLE INSTR + DST REG + SRC REG
  public LdrInstruction(Register destReg, Register srcReg) {
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = 0;
  }

  //SIMPLE INSTR + DST REG + SRC REG + OFFSET
  public LdrInstruction(Register destReg, Register srcReg, int immOffset) {
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = immOffset;
  }


  //COND INSTR + DST REG + SRC REG
  public LdrInstruction(ConditionCode conditionCode, Register destReg, Register srcReg) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = 0;
  }

  //COND INSTR + DST REG + SRC REG + OFFSET
  public LdrInstruction(ConditionCode conditionCode, Register destReg, Register srcReg, int immOffset) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = immOffset;
  }

  public void setIsLDRSB() {
    isLDRSB = true;
  }

  @Override
  public String writeInstruction() {
    return printer.printLDR(destReg,
        conditionCode,
        immOffset,
        srcReg,
        constant,
        isLDRSB);
  }
}
