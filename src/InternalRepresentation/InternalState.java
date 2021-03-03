package InternalRepresentation;

import static InternalRepresentation.Enums.Directive.*;
import static InternalRepresentation.Enums.Register.*;

import AbstractSyntaxTree.ProgramNode;
import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Instructions.BuiltInLabelInstruction;
import InternalRepresentation.Instructions.DirectiveInstruction;
import InternalRepresentation.Enums.ArithmeticOperation;
import InternalRepresentation.Instructions.ArithmeticInstruction;
import InternalRepresentation.Instructions.Instruction;

import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.LineBreak;
import InternalRepresentation.Instructions.MsgInstruction;
import InternalRepresentation.Enums.Register;
import SemanticAnalysis.SymbolTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class InternalState {

  private final Set<String> declaredLabels;
  private final List<Instruction> generatedInstructions;
  private final int MAX_STACK_ARITHMETIC_SIZE = 1024;
  int argStackOffset = 0;
  private List<BuiltInFunction> builtInLabels;
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
    prevResult = null;
    builtInLabels = BuiltInFunction.getUsed();

    labelCount = 0;
  }

  public void generateAssembly(File output, ProgramNode programNode) {
    try {
      FileWriter writer = new FileWriter(output);


      programNode.generateAssembly(this);

      CustomBuiltInFunctions customBuiltInFunctions = new CustomBuiltInFunctions();
      List<Instruction> instructions = new ArrayList<>();
      builtInLabels = BuiltInFunction.getUsed();
      for (BuiltInFunction label : builtInLabels) {
        instructions.addAll(customBuiltInFunctions.generateAssembly(label));
      }

      if (!MsgInstruction.getMessages().isEmpty()) {
        writer.write(new DirectiveInstruction(DATA).writeInstruction());
        writer.write(new LineBreak().writeInstruction());
        writer.write(new LineBreak().writeInstruction());
        // add all generated messages
        for (MsgInstruction msg : MsgInstruction.getMessages()) {
          writer.write(msg.toString() + ":\n");
          writer.write(msg.writeInstruction());
          writer.write(new LineBreak().writeInstruction());
        }
      }

      writer.write(new DirectiveInstruction(TEXT).writeInstruction());
      writer.write(new LineBreak().writeInstruction());

      writer.write(new DirectiveInstruction(GLOBAL, "main").writeInstruction());

      // add all generated instructions
      for (Instruction instruction : generatedInstructions) {
        writer.write(instruction.writeInstruction());
      }
      // add all used built in functions

      for (Instruction instruction : instructions) {
        writer.write(instruction.writeInstruction());
      }

      writer.close();

    } catch (IOException e) {
      System.out.println("Could not write to file: " + output.getName());
    }
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
    registers.push(R12);
    registers.push(R11);
    registers.push(R10);
    registers.push(R9);
    registers.push(R8);
    registers.push(R7);
    registers.push(R6);
    registers.push(R5);
    registers.push(R4);
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

  public Register getPrevResult() {
    return prevResult;
  }

  public void setPrevResult(Register destination) {
    prevResult = destination;
  }

  public void addBuiltInLabel(BuiltInFunction function) {
    function.setUsed();
  }

  public void allocateStackSpace(SymbolTable symbolTable) {
    int size = symbolTable.getVarsSize();
    argStackOffset = symbolTable.getVarsSize();
    while (size > 0) {
      addInstruction(new ArithmeticInstruction(ArithmeticOperation.SUB, SP, SP,
          new Operand(Math.min(size, MAX_STACK_ARITHMETIC_SIZE)), false));
      size -= Math.min(size, MAX_STACK_ARITHMETIC_SIZE);
    }
  }

  public void deallocateStackSpace(SymbolTable symbolTable) {
    int size = symbolTable.getVarsSize();
    while (size > 0) {
      addInstruction(new ArithmeticInstruction(ArithmeticOperation.ADD, SP, SP,
          new Operand(Math.min(size, MAX_STACK_ARITHMETIC_SIZE)), false));
      size -= Math.min(size, MAX_STACK_ARITHMETIC_SIZE);
    }
  }

  public void decrementArgStackOffset(int argSize) {
    argStackOffset -= argSize;
  }

  public int getArgStackOffset() {
    return argStackOffset;
  }
}