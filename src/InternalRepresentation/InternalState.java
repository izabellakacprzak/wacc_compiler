package InternalRepresentation;

import static InternalRepresentation.Enums.Reg.*;

import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Instructions.Instruction;

import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.MsgInstruction;
import InternalRepresentation.Enums.Reg;

import java.util.*;

public class InternalState {

  private final Set<String> declaredLabels;
  private final List<Instruction> generatedInstructions;
  private final List<MsgInstruction> messages;
  private final List<LabelInstruction> builtInLabels;
  private Stack<Register> availableRegs;
  private int labelCount;
  private Register prevResult;



  public InternalState() {
    // setup stack function
    resetAvailableRegs();
    //TODO check callee reserved regs => main func starts from R4
    //TODO change this to a stack!!!!
    declaredLabels = new HashSet<>();
    generatedInstructions = new ArrayList<>();
    messages = new ArrayList<>();
    prevResult = null;
    builtInLabels = new ArrayList<>();

    labelCount = 0;
  }


  //TODO change the availableRegs list to a stack
  public Register peekFreeRegister() {
    //TODO check if we can ever run out of regs
    return availableRegs.peek();
  }

  public void pushFreeRegister(Register reg) {
    availableRegs.push(reg);

  }

  //public Register popFreeRegister() {
   // return availableRegs.pop();
 // }


  public Register popFreeRegister() {
    if (!availableRegs.isEmpty()) {
      return availableRegs.pop();
    } else {
      // use stack
      return null;
    }
  }

  public void addInstruction(Instruction instruction) {
    generatedInstructions.add(instruction);
  }

  public void resetAvailableRegs() {
    Stack<Register> registers = new Stack<>();
    registers.push(new Register(R12));
    registers.push(new Register(R11));
    registers.push(new Register(R10));
    registers.push(new Register(R9));
    registers.push(new Register(R8));
    registers.push(new Register(R7));
    registers.push(new Register(R6));
    registers.push(new Register(R5));
    registers.push(new Register(R4));
    this.availableRegs = registers;
  }

  public Stack<Register> getAvailableRegs() {
    return availableRegs;
  }

  public void setAvailableRegs(Stack<Register> availableRegs) {
    this.availableRegs = availableRegs;
  }

  /*public void setAsUsed(Register register) {
    availableRegs.remove(register);
  }
*/
//  public void setAsUnused(Register register) {
//
//      availableRegs.push(register);
//    }
//  }

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