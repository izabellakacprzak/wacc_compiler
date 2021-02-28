package InternalRepresentation;

import java.util.List;

public class InstructionPrinter {

  public String printArithmetic(String operationType, Register destReg,
                                Register operand1, Operand operand2, boolean setBits) {
    StringBuilder instruction = new StringBuilder(operationType);
    if (setBits) {
      instruction.append("S");
    }

    instruction.append(" ").
            append(destReg.getRegName()).
            append(operand1.getRegName()).
            append(operand2.toString());

    return instruction.toString();
  }

  public String printCompare(Register operand1, Operand operand2) {
    return "CMP " + operand1.getRegName() + ", " + operand2.toString();
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

  public String printLDR(Register destReg, ConditionCode conditionCode,
                         int immOffset, Register srcReg,
                         String constant, boolean isLDRSB) {
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

  public String printLogical(String operationType, Register destReg,
                             Register operand1, Operand operand2) {
    return operationType + " " + destReg.getRegName() + ", " +
            operand1.getRegName() + ", " +
            operand2.toString();
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
}
