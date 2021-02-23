package InternalRepresentation;

import java.util.List;

public class InstructionPrinter {

  public String printAnd(Register destReg, Register operand1, Register operand2) {
    return "AND " + destReg.getRegName() + ", " +
            operand1.getRegName() + ", " +
            operand2.getRegName();
  }

  public String printCompare(Register operand1, Register operand2) {
    return "CMP " + operand1.getRegName() + ", " + operand2.getRegName();
  }

  public String printBranch(List<ConditionCode> conditionCodes, String label) {
    StringBuilder instruction = new StringBuilder("B");
    if (conditionCodes != null) {
      for (ConditionCode conditionCode : conditionCodes) {
        instruction.append(conditionCode.getCondName());
      }
    }
    instruction.append(" ").append(label);
    return instruction.toString();
  }

  public String printLabel(String label) {
    return label + ":";
  }

  public String printLDR(Register destReg,
                         ConditionCode conditionCode,
                         int immOffset,
                         Register srcReg,
                         String constant,
                         boolean isLDRSB) {
    String instruction = (isLDRSB) ? "LDRSB" : "LDR";

    if (conditionCode != null) {
      instruction += conditionCode.getCondName();
    }

    instruction += " " + destReg.getRegName() + ", ";

    if (constant == null) {
      instruction += "[" + srcReg.getRegName();
      instruction += (immOffset == 0) ? "]" : ", " + "#" + immOffset + "]";
    } else {
      instruction += "=" + constant;
    }

    return instruction;
  }

  public String printMOV(ConditionCode conditionCode, Register destReg, Register srcReg,
                         int intImmediate, char charImmediate) {
    String instruction = "MOV";

    if (conditionCode != null) {
      instruction += conditionCode.getCondName();
    }

    instruction += " " + destReg + ", ";

    if (srcReg != null) {
      instruction += srcReg.getRegName();
      return instruction;
    }
    if (intImmediate != -1) {
      instruction += '#' + Integer.toString(intImmediate);
      return instruction;
    }

    if (charImmediate == '\0') {
      instruction += "#0";
      return instruction;
    }

    instruction += "#'" + charImmediate + "'";
    return instruction;
  }

  public String printOr(Register destReg, Register operand1, Register operand2) {
    return "OR " + destReg.getRegName() + ", " +
            operand1.getRegName() + ", " +
            operand2.getRegName();
  }

  public String printPop(Register reg) {
    return "POP {" + reg.getRegName() + "}";
  }

  public String printPush(Register reg) {
    return "PUSH {" + reg.getRegName() + "}";
  }

  public String printSMull(Register destReg1, Register destReg2, Register operand1, Register operand2) {
    return "SMULL " + destReg1.getRegName() + ", " +
            destReg2.getRegName() + ", " +
            operand1.getRegName() + ", " +
            operand2.getRegName();
  }

  public String printXOr(Register destReg, Register operand1, Register operand2) {
    return "EOR " + destReg.getRegName() + ", " +
            operand1.getRegName() + ", " +
            operand2.getRegName();
  }

}
