package InternalRepresentation.Instructions;

import InternalRepresentation.ConditionCode;
import InternalRepresentation.Enums.LdrType;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Instructions.Instruction;
import InternalRepresentation.Register;

public class LdrInstruction implements Instruction {
  private final Register destReg;
  private final InstructionPrinter printer = new InstructionPrinter();
  private ConditionCode conditionCode;
  private int immOffset;
  private Register srcReg;
  private String constant = null;
  private LdrType type = LdrType.LDR; //default

  //SIMPLE INSTR + DST REG + STRING CONSTANT
  public LdrInstruction(LdrType type, Register destReg, String constant) {
    this.type = type;
    this.destReg = destReg;
    this.constant = constant;
  }

  //SIMPLE INSTR + DST REG + INT CONSTANT
  public LdrInstruction(LdrType type, Register destReg, int constant) {
    this.type = type;
    this.destReg = destReg;
    this.constant = Integer.toString(constant);
  }


  //COND INSTR + DST REG + STRING CONSTANT
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg, String constant) {
    this.type = type;
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.constant = constant;
  }

  //COND INSTR + DST REG + INT CONSTANT
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg, int constant) {
    this.type = type;
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.constant = Integer.toString(constant);
  }


  //SIMPLE INSTR + DST REG + SRC REG
  public LdrInstruction(LdrType type, Register destReg, Register srcReg) {
    this.type = type;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = 0;
  }

  //SIMPLE INSTR + DST REG + SRC REG + OFFSET
  public LdrInstruction(LdrType type, Register destReg, Register srcReg, int immOffset) {
    this.type = type;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = immOffset;
  }


  //COND INSTR + DST REG + SRC REG
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg, Register srcReg) {
    this.type = type;
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = 0;
  }

  //COND INSTR + DST REG + SRC REG + OFFSET
  public LdrInstruction(LdrType type, ConditionCode conditionCode, Register destReg, Register srcReg, int immOffset) {
    this.type = type;
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.srcReg = srcReg;
    this.immOffset = immOffset;
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
}
