package InternalRepresentation;

import static InternalRepresentation.Enums.Directive.*;
import static InternalRepresentation.Enums.Register.*;

import AbstractSyntaxTree.ProgramNode;
import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Instructions.DirectiveInstruction;
import InternalRepresentation.Enums.ArithmeticOperation;
import InternalRepresentation.Instructions.ArithmeticInstruction;
import InternalRepresentation.Instructions.Instruction;
import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.MsgInstruction;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.PopInstruction;
import InternalRepresentation.Instructions.PushInstruction;
import SemanticAnalysis.SymbolTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class InternalState {

  private static final int MAX_STACK_ARITHMETIC_SIZE = 1024;
  private static final String LINE_BREAK = "\n";
  private static final String TAB = "\t";

  private final List<Instruction> generatedInstructions;
  private Stack<Register> availableRegs;
  private int argStackOffset = 0;
  private int labelCount;

  public InternalState() {
    // setup stack function
    resetAvailableRegs();
    //TODO check callee reserved regs => main func starts from R4
    //TODO change this to a stack!!!!
    generatedInstructions = new ArrayList<>();
    labelCount = 0;
  }

  public void generateAssembly(File output, ProgramNode programNode) {
    try {
      FileWriter writer = new FileWriter(output);

      programNode.generateAssembly(this);

      CustomBuiltInFunctions customBuiltInFunctions = new CustomBuiltInFunctions();
      List<Instruction> instructions = new ArrayList<>();

      for (BuiltInFunction label : BuiltInFunction.getUsed()) {
        instructions.addAll(customBuiltInFunctions.generateAssembly(label));
      }

      if (!MsgInstruction.getMessages().isEmpty()) {
        writer.write(new DirectiveInstruction(DATA).writeInstruction());
        writer.write(LINE_BREAK);
        writer.write(LINE_BREAK);
        // add all generated messages
        for (MsgInstruction msg : MsgInstruction.getMessages()) {
          writer.write(msg.toString() + ":\n\t");
          writer.write(msg.writeInstruction());
          writer.write(LINE_BREAK);
        }

        writer.write(LINE_BREAK);
      }

      writer.write(new DirectiveInstruction(TEXT).writeInstruction());
      writer.write(LINE_BREAK);
      writer.write(LINE_BREAK);

      writer.write(new DirectiveInstruction(GLOBAL, "main").writeInstruction());
      writer.write(LINE_BREAK);

      // add all generated instructions
      for (Instruction instruction : generatedInstructions) {
        if (!(instruction instanceof LabelInstruction)) {
          writer.write(TAB);
        }

        writer.write(instruction.writeInstruction());
        writer.write(LINE_BREAK);
      }

      // add all used built in functions
      for (Instruction instruction : instructions) {
        if (!(instruction instanceof LabelInstruction)) {
          writer.write(TAB);
        }

        writer.write(instruction.writeInstruction());
        writer.write(LINE_BREAK);
      }

      writer.close();

    } catch (IOException e) {
      System.out.println("Could not write to file: " + output.getName());
    }
  }

  public Register peekFreeRegister() {
    Register nextReg = availableRegs.peek();

    if (availableRegs.size() <= NUM_STACK_REGS) {
      addInstruction(new PushInstruction(LAST_LOAD_REG));
      availableRegs.push(LAST_LOAD_REG);
      return LAST_LOAD_REG;
    }

    return nextReg;
  }

  public void pushFreeRegister(Register reg) {
    availableRegs.push(reg);
  }

  public Register popFreeRegister() {
    if (availableRegs.size() <= NUM_STACK_REGS) {
      addInstruction(new PushInstruction(LAST_LOAD_REG));
      return LAST_LOAD_REG;
    }

    return availableRegs.pop();
  }

  public Register popRegFromStack() {
    //TODO: is this check meaningless?: if (availableRegs.isEmpty())
    Register popReg = availableRegs.pop();

    addInstruction(new PopInstruction(popReg));
    return popReg;
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

  public String generateNewLabel() {
    String newLabel = "L" + labelCount;
    labelCount++;
    return newLabel;
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

  public void incrementArgStackOffset(int argSize) {
    argStackOffset += argSize;
  }

  public int getArgStackOffset() {
    return argStackOffset;
  }

  public void resetArgStackOffset(int argStackOffset) {
    this.argStackOffset = argStackOffset;
  }
}