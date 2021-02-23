package InternalRepresentation;

import java.util.ArrayList;
import java.util.List;

public class Branch implements Instruction {
  private final InstructionPrinter printer = new InstructionPrinter();
  private final String label;
  private List<ConditionCode> conditionCodes;

  //SIMPLE INSTR + LABEL
  public Branch(String label) {
    this.label = label;
  }

  //1 COND INSTR + LABEL
  public Branch(ConditionCode conditionCode, String label) {
    this.label = label;
    conditionCodes = new ArrayList<>();
    conditionCodes.add(conditionCode);
  }

  //MULTIPLE COND INSTR + LABEL
  public Branch(List<ConditionCode> conditionCodes, String label) {
    this.label = label;
    this.conditionCodes = conditionCodes;
  }

  @Override
  public String writeInstruction() {
    return printer.printBranch(conditionCodes, label);
  }
}
