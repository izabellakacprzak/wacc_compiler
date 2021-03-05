package InternalRepresentation.Instructions;

import InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn;
import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.InstructionPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BranchInstruction implements Instruction {
  private final InstructionPrinter printer = new InstructionPrinter();
  private final String label;
  private final List<ConditionCode> conditionCodes;
  private final BranchOperation operation;

  //MULTIPLE COND INSTR + LABEL
  public BranchInstruction(List<ConditionCode> conditionCodes, BranchOperation operation,
      String label) {

    assert conditionCodes != null;
    assert operation != null;
    assert label != null;

    this.label = label;
    this.conditionCodes = conditionCodes;
    this.operation = operation;
  }

  //SIMPLE INSTR + LABEL
  public BranchInstruction(BranchOperation operation, String label) {
    this(new ArrayList<>(), operation, label);
  }

  //1 COND INSTR + LABEL
  public BranchInstruction(ConditionCode conditionCode, BranchOperation operation, String label) {
    this(new ArrayList<>(Collections.singleton(conditionCode)), operation, label);
  }

  public BranchInstruction(ConditionCode conditionCode, BranchOperation operation,
      CustomBuiltIn function) {
    this(conditionCode, operation, function.getLabel());
    function.setUsed();
  }

  public BranchInstruction(BranchOperation operation, CustomBuiltIn function) {
    this(operation, function.getLabel());
    function.setUsed();
  }

  @Override
  public String writeInstruction() {
    return printer.printBranch(operation, conditionCodes, label);
  }

  public enum BranchOperation {B, BL, BX, SWI}
}
