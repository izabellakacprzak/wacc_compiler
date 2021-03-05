package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn;
import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.InstructionPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BranchInstruction implements Instruction {
  /* printer:         instruction printer used for generating String representations of instructions
   * label:           destination label of the branch instruction
   * conditionCodes:  condition codes for the branch instruction
   * operation:       type of branch instruction
   */
  private final InstructionPrinter printer = new InstructionPrinter();
  private final String label;
  private final List<ConditionCode> conditionCodes;
  private final BranchOperation operation;

  /* MULTIPLE CONDITIONS INSTRUCTION + LABEL */
  public BranchInstruction(List<ConditionCode> conditionCodes, BranchOperation operation,
      String label) {

    assert conditionCodes != null;
    assert operation != null;
    assert label != null;

    this.label = label;
    this.conditionCodes = conditionCodes;
    this.operation = operation;
  }

  /* SIMPLE INSTRUCTION + LABEL */
  public BranchInstruction(BranchOperation operation, String label) {
    this(new ArrayList<>(), operation, label);
  }

  /* ONE CONDITION INSTRUCTION + LABEL */
  public BranchInstruction(ConditionCode conditionCode, BranchOperation operation, String label) {
    this(new ArrayList<>(Collections.singleton(conditionCode)), operation, label);
  }

  /* ONE CONDITION INSTRUCTION + BUILTIN FUNCTION BRANCH */
  public BranchInstruction(ConditionCode conditionCode, BranchOperation operation,
      CustomBuiltIn function) {
    this(conditionCode, operation, function.getLabel());
    function.setUsed();
  }

  /* SIMPLE BUILTIN BRANCH */
  public BranchInstruction(BranchOperation operation, CustomBuiltIn function) {
    this(operation, function.getLabel());
    function.setUsed();
  }

  /* Generates string representation of ARM instruction */
  @Override
  public String writeInstruction() {
    return printer.printBranch(operation, conditionCodes, label);
  }

  /* Type of branch instruction */
  public enum BranchOperation {B, BL}
}
