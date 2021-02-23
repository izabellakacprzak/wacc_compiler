package InternalRepresentation;

import java.util.List;

public class InstructionPrinter {


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


}
