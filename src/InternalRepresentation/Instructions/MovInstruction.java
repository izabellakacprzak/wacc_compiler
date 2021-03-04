package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.ConditionCode;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.InstructionPrinter;

public class MovInstruction implements Instruction {
  private final Register destReg;
  private Register srcReg = null;
  private ConditionCode conditionCode = null;
  private Integer intImmediate = null;
  private char charImmediate = '\u0000';

  private final InstructionPrinter printer = new InstructionPrinter();
  /*
  TODO: delete this comment - MOV constructor taken after assembly code
     in folder IO/print
   */

  //SIMPLE INSTR + DEST REG + INT
  public MovInstruction(Register destReg, int intImmediate) {
    this.destReg = destReg;
    this.intImmediate = intImmediate;
  }

  //SIMPLE INSTR + DEST REG + CHAR
  public MovInstruction(Register destReg, char charImmediate) {
    this.destReg = destReg;
    this.charImmediate = charImmediate;
  }

  //SIMPLE INSTR + DEST REG + SRC REG
  public MovInstruction(Register destReg, Register srcReg) {
    this.destReg = destReg;
    this.srcReg = srcReg;
  }

  //COND INSTR + DEST REG + INT
  public MovInstruction(ConditionCode conditionCode, Register destReg, int intImmediate) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.intImmediate = intImmediate;
  }

  //COND INSTR + DEST REG + CHAR
  public MovInstruction(ConditionCode conditionCode, Register destReg, char charImmediate) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.charImmediate = charImmediate;
  }

  //COND INSTR + DEST REG + SRC REG
  public MovInstruction(ConditionCode conditionCode, Register destReg, Register srcReg) {
    this.conditionCode = conditionCode;
    this.destReg = destReg;
    this.srcReg = srcReg;
  }

  public String writeInstruction() {
    return printer.printMOV(conditionCode, destReg, srcReg, intImmediate, charImmediate);
  }

}
