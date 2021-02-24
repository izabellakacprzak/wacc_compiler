package InternalRepresentation.Instructions;

import InternalRepresentation.ConditionCode;
import InternalRepresentation.InstructionPrinter;

import java.util.ArrayList;
import java.util.List;

public class BranchInstruction implements Instruction {
  private final InstructionPrinter printer = new InstructionPrinter();
  private final String label;
  private List<ConditionCode> conditionCodes;

  //SIMPLE INSTR + LABEL
  public BranchInstruction(String label) {
    this.label = label;
  }

  //1 COND INSTR + LABEL
  public BranchInstruction(ConditionCode conditionCode, String label) {
    this.label = label;
    conditionCodes = new ArrayList<>();
    conditionCodes.add(conditionCode);
  }

  //MULTIPLE COND INSTR + LABEL
  public BranchInstruction(List<ConditionCode> conditionCodes, String label) {
    this.label = label;
    this.conditionCodes = conditionCodes;
  }

  @Override
  public String writeInstruction() {
    return printer.printBranch(conditionCodes, label);
  }
}
