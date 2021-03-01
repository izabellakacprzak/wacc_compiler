package InternalRepresentation;

import InternalRepresentation.Instructions.CustomBuiltInInstruction.BuiltInFunction;
import InternalRepresentation.Instructions.Instruction;

import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.MsgInstruction;
import InternalRepresentation.Register.Reg;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InternalState {

  private List<Register> availableRegs;
  private int labelCount;
  private final Set<String> declaredLabels;
  private final List<Instruction> generatedInstructions;
  private final List<MsgInstruction> messages;
  private Register prevResult;
  private final List<LabelInstruction> builtInLabels;

  public InternalState() {
    // setup stack function
    availableRegs = new ArrayList<>();
    declaredLabels = new HashSet<>();
    generatedInstructions = new ArrayList<>();
    messages = new ArrayList<>();
    prevResult = null;
    builtInLabels = new ArrayList<>();

    labelCount = 0;
  }

  public Register getFreeRegister() {
    if (!availableRegs.isEmpty()) {
      Register availableRegister = availableRegs.get(0);
      availableRegs.remove(0);
      return availableRegister;
    } else {
      // use stack
      return null;
    }
  }

  public void addInstruction(Instruction instruction) {
    generatedInstructions.add(instruction);
  }

  public void resetAvailableRegs() {
    List<Register> registers = new ArrayList<>();

    registers.add(new Register(Register.getDestReg()));
    for (Reg reg : Register.getParamRegs()) {
      registers.add(new Register(reg));
    }

    this.availableRegs = registers;
  }

  public void setAvailableRegs(List<Register> availableRegs) {
    this.availableRegs = availableRegs;
  }

  public List<Register> getAvailableRegs() {
    return availableRegs;
  }

  public void setAsUsed(Register register) {
    availableRegs.remove(register);
  }

  public void setAsUnused(Register register) {
    if (!availableRegs.contains(register)) {
      availableRegs.add(register);
    }
  }

  public String generateNewLabel() {
    String newLabel = "L" + labelCount;
    declaredLabels.add(newLabel);
    labelCount++;
    return newLabel;
  }

  public String getMsg(String value) {
    MsgInstruction message = new MsgInstruction(value.length() + 1, value);
    messages.add(message);
    return "msg_" + (messages.size() - 1);
  }

  public Register getPrevResult() {
    return prevResult;
  }

  public void setPrevResult(Register destination) {
    prevResult = destination;
  }

  public void addBuiltInLabel(BuiltInFunction function) {
    function.setUsed();
  }
}