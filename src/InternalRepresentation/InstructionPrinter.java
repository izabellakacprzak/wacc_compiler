package InternalRepresentation;

import InternalRepresentation.Enums.*;

import java.util.List;

public class InstructionPrinter {

  public String printArithmetic(ArithmeticOperation operationType, Register destReg,
      Register operand1, Operand operand2, boolean setBits) {
    StringBuilder instruction = new StringBuilder(operationType.toString());
    if (setBits) {
      instruction.append("S");
    }

    instruction.append(" ").
        append(destReg.getRegName()).
        append(", ").
        append(operand1.getRegName()).
        append(", ").
        append(operand2.toString());

    return instruction.toString() + "\n";
  }

  public String printCompare(Register operand1, Operand operand2) {
    return "CMP " + operand1.getRegName() + ", " + operand2.toString() + "\n";
  }

  public String printBranch(BranchOperation operation, List<ConditionCode> conditionCodes, String label) {
    StringBuilder instruction = new StringBuilder(operation.toString());
    if (conditionCodes != null) {
      for (ConditionCode conditionCode : conditionCodes) {
        instruction.append(conditionCode.getCondName());
      }
    }
    instruction.append(" ").append(label);
    return instruction.toString() + "\n";
  }

  public String printLabel(String label) {
    return label + ": \n" ;
  }

  public String printLDR(Register destReg, ConditionCode conditionCode,
      int immOffset, Register srcReg,
      String constant, LdrType type) {
    String instruction = type.name();

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

    return instruction + "\n";
  }

  public String printLogical(LogicalOperation operationType, Register destReg,
      Register operand1, Operand operand2) {
    return operationType.toString() + " " + destReg.getRegName() + ", " +
        operand1.getRegName() + ", " +
        operand2.toString() + "\n";
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
      return instruction + "\n";
    }
    if (intImmediate != -1) {
      instruction += '#' + Integer.toString(intImmediate);
      return instruction + "\n";
    }

    if (charImmediate == '\0') {
      instruction += "#0";
      return instruction + "\n";
    }

    instruction += "#'" + charImmediate + "'";
    return instruction + "\n";
  }

  public String printPop(Register reg) {
    return "POP {" + reg.getRegName() + "}" + "\n";
  }

  public String printPush(Register reg) {
    return "PUSH {" + reg.getRegName() + "}" + "\n";
  }

  public String printSMull(Register destReg1, Register destReg2, Register operand1,
      Register operand2) {
    return "SMULL " + destReg1.getRegName() + ", " +
        destReg2.getRegName() + ", " +
        operand1.getRegName() + ", " +
        operand2.getRegName() + "\n";
  }

  public String printStr(StrType type, Register destReg,
      Register offsetReg1, Register offsetReg2, int offsetImm, boolean useExclamation) {
    StringBuilder instruction = new StringBuilder(type.toString());

    instruction.append(" ").
        append(destReg.getRegName()).
        append(", [").
        append(offsetReg1.getRegName());

    if (offsetReg2 != null) {
      instruction.append(", ").append(offsetReg2.getRegName());
    } else {
      if (offsetImm != 0) {
        instruction.append(", ").append("#");
        switch (type) {
          case STRB:
            instruction.append(offsetImm);
          case STRH:
            instruction.append(offsetImm * 2);
          default:
            instruction.append(offsetImm * 4);
        }
      }
    }
    instruction.append("]");
    if (useExclamation) {
      instruction.append("!");
    }
    return instruction.toString() + "\n";
  }
}
