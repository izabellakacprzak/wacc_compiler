package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Enums.ConditionCode;
import InternalRepresentation.InstructionPrinter;

import java.util.ArrayList;
import java.util.List;

public class BranchInstruction implements Instruction {
  private final InstructionPrinter printer = new InstructionPrinter();
  private final String label;
  private List<ConditionCode> conditionCodes;
  private final BranchOperation operation;

  //SIMPLE INSTR + LABEL
  public BranchInstruction(BranchOperation operation, String label) {
    this.label = label;
    this.operation = operation;
  }

  //1 COND INSTR + LABEL
  public BranchInstruction(ConditionCode conditionCode, BranchOperation operation, String label) {
    this.label = label;
    conditionCodes = new ArrayList<>();
    conditionCodes.add(conditionCode);
    this.operation = operation;
  }

  //MULTIPLE COND INSTR + LABEL
  public BranchInstruction(List<ConditionCode> conditionCodes, BranchOperation operation,
      String label) {
    this.label = label;
    this.conditionCodes = conditionCodes;
    this.operation = operation;
  }

  @Override
  public String writeInstruction() {
    return printer.printBranch(conditionCodes, label);
  }

}
