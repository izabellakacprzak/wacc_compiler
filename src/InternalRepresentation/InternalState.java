package InternalRepresentation;

import static InternalRepresentation.Instructions.DirectiveInstruction.Directive.*;
import static InternalRepresentation.Utils.Register.*;

import AbstractSyntaxTree.ProgramNode;
import InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn;
import InternalRepresentation.Instructions.DirectiveInstruction;
import InternalRepresentation.Instructions.ArithmeticInstruction.ArithmeticOperation;
import InternalRepresentation.Instructions.ArithmeticInstruction;
import InternalRepresentation.Instructions.Instruction;
import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.MsgInstruction;
import InternalRepresentation.Instructions.PopInstruction;
import InternalRepresentation.Instructions.PushInstruction;
import InternalRepresentation.Utils.CustomBuiltInGenerator;
import InternalRepresentation.Utils.Operand;
import InternalRepresentation.Utils.Register;
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
  private final CodeGenVisitor codeGenVisitor;
  private int varSize = 0;
  private Stack<Register> availableRegs;
  private int paramStackOffset = 0;
  private int argStackOffset = 0;
  private int labelCount;
  private SymbolTable funcSymTable;

  public InternalState() {
    // setup stack function
    resetAvailableRegs();
    generatedInstructions = new ArrayList<>();
    codeGenVisitor = new CodeGenVisitor();
    labelCount = 0;
  }

  public void generateAssembly(File output, ProgramNode programNode) {
    try {
      FileWriter writer = new FileWriter(output);
      List<Instruction> instructions = new ArrayList<>();

      programNode.generateAssembly(this);

      for (CustomBuiltIn label : CustomBuiltIn.getUsed()) {
        instructions.addAll(CustomBuiltInGenerator.generateAssembly(label));
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


  public CodeGenVisitor getCodeGenVisitor() {
    return codeGenVisitor;
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
    Register popReg = availableRegs.pop();

    addInstruction(new PopInstruction(popReg));
    return popReg;
  }

  public void addInstruction(Instruction instruction) {
    generatedInstructions.add(instruction);
  }

  public void resetAvailableRegs() {
    Stack<Register> registers = new Stack<>();
    List<Register> paramRegs = getParamRegs();
    Collections.reverse(paramRegs);

    for (Register reg : paramRegs) {
      registers.push(reg);
    }

    this.availableRegs = registers;
  }

  public String generateNewLabel() {
    String newLabel = "L" + labelCount;
    labelCount++;
    return newLabel;
  }

  public void allocateStackSpace(SymbolTable symbolTable) {
    int size = symbolTable.getVarsSize();
    argStackOffset += symbolTable.getVarsSize();
    varSize += symbolTable.getVarsSize();
    while (size > 0) {
      addInstruction(new ArithmeticInstruction(ArithmeticOperation.SUB, SP, SP,
          new Operand(Math.min(size, MAX_STACK_ARITHMETIC_SIZE)), false));
      size -= Math.min(size, MAX_STACK_ARITHMETIC_SIZE);
    }
  }

  public void deallocateStackSpace(SymbolTable symbolTable) {
    int size = symbolTable.getVarsSize();
//    varSize -= symbolTable.getVarsSize();
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

  public int decrementParamStackOffset(int argSize, int offset) {
    int newOff = funcSymTable.updateOffsetPerVar(argSize, offset);
    paramStackOffset -= argSize;
    return newOff;
  }

  public void incrementParamStackOffset(int argSize) {
    paramStackOffset += argSize;
  }

  public int getArgStackOffset() {
    return argStackOffset;
  }

  public void resetArgStackOffset(int argStackOffset) {
    this.argStackOffset = argStackOffset;
  }

  public SymbolTable getFunctionSymTable() {
    return funcSymTable;
  }

  public void setFunctionSymTable(SymbolTable funcSymTable) {
    this.funcSymTable = funcSymTable;
  }

  public int getParamStackOffset() {
    return paramStackOffset;
  }

  public void resetParamStackOffset() {
    paramStackOffset = 0;
  }

  public void resetParamStackOffset(int size) {
    paramStackOffset = size;
  }

  public int getVarSize() {
    return varSize;
  }
  public void incrementVarSize(int size){
  varSize += size;
  }
}