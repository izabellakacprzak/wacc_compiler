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
  private int varSize = 0;                /* stores the size of variables on the stack */
  private Stack<Register> availableRegs;
  /* Global stack offset of declared variables */
  private int stackOffset = 0;
  private int declaredArgStackOffset = 0;
  //private int argStackOffset = 0;         /* stack pointer for variables offset calculation */
  private int labelCount;
  private SymbolTable funcSymTable;       /* points to the function symbol table in order to deallocate
                                             the variables off the stack at scope closing */

  /* The internal states stores the generated instructions list, the available registers to use,
   * a reference to a CodeGenVisitor object to generate instructions and a label count for labels generation */
  public InternalState() {
    resetAvailableRegs();
    generatedInstructions = new ArrayList<>();
    codeGenVisitor = new CodeGenVisitor();
    labelCount = 0;
  }

  /* Generate assembly starts from the top (the Program Node) and traverses the previously generated AST
   * to generate the assembly code for all of the nodes. */
  public void generateAssembly(File output, ProgramNode programNode) {
    try {
      FileWriter writer = new FileWriter(output);
      List<Instruction> instructions = new ArrayList<>();

      /* generate program assembly code by traversing the AST and store the
       * instructions in the generatedInstructions list of the internal state */
      programNode.generateAssembly(this);

      /*add the used built-in functions code used to the instructions list  */
      for (CustomBuiltIn label : CustomBuiltIn.getUsed()) {
        instructions.addAll(CustomBuiltInGenerator.generateAssembly(label));
      }

      /* add messages and output them to the .s file */
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

      /* write the generated assembly instructions to the .s file */
      for (Instruction instruction : generatedInstructions) {
        if (!(instruction instanceof LabelInstruction)) {
          writer.write(TAB);
        }

        writer.write(instruction.writeInstruction());
        writer.write(LINE_BREAK);
      }

      /* write the built-in functions assembly instructions to the .s file */
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

  /* return the first available register to use without removing it from the available
   * registers stack */
  public Register peekFreeRegister() {
    Register nextReg = availableRegs.peek();

    if (availableRegs.size() <= NUM_STACK_REGS) {
      addInstruction(new PushInstruction(LAST_LOAD_REG));
      availableRegs.push(LAST_LOAD_REG);
      return LAST_LOAD_REG;
    }

    return nextReg;
  }

  /* add a register to be used back on the available registers stack */
  public void pushFreeRegister(Register reg) {
    availableRegs.push(reg);
  }

  /* return the first available register to use and remove it from the available
   * registers stack */
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

  /*adds instruction to the list of generated instructions*/
  public void addInstruction(Instruction instruction) {
    generatedInstructions.add(instruction);
  }

  /*makes all registers to be used available*/
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

  /*calculates the variables sizes in the symbol table and allocates stack space for them,
   increases the varSize variable */
  public void allocateStackSpace(SymbolTable symbolTable) {
    int size = symbolTable.getVarsSize();
    symbolTable.incrementDeclaredParamsOffset(size);
    symbolTable.incrementDeclaredVarsOffset(size);
    stackOffset += size;

    while (size > 0) {
      addInstruction(new ArithmeticInstruction(ArithmeticOperation.SUB, SP, SP,
          new Operand(Math.min(size, MAX_STACK_ARITHMETIC_SIZE)), false));
      size -= Math.min(size, MAX_STACK_ARITHMETIC_SIZE);
    }
  }

  /*calculates the variables sizes in the symbol table and de-allocates stack space from them */
  public void deallocateStackSpace(SymbolTable symbolTable) {
    int size = symbolTable.getVarsSize();
    stackOffset -= size;

    while (size > 0) {
      addInstruction(new ArithmeticInstruction(ArithmeticOperation.ADD, SP, SP,
          new Operand(Math.min(size, MAX_STACK_ARITHMETIC_SIZE)), false));
      size -= Math.min(size, MAX_STACK_ARITHMETIC_SIZE);
    }
  }

  public void decrementStackOffset(int argSize, int offset) {
    //funcSymTable.updateOffsetPerVar(argSize, offset);
    stackOffset -= argSize;
  }

  public void incrementStackOffset(int argSize) {
    stackOffset += argSize;
  }

//  public int getArgStackOffset() {
//    return argStackOffset;
//  }
//
//  public void resetArgStackOffset(int argStackOffset) {
//    this.argStackOffset = argStackOffset;
//  }

  public SymbolTable getFunctionSymTable() {
    return funcSymTable;
  }

  /* sets the current scope */
  public void setFunctionSymTable(SymbolTable funcSymTable) {
    this.funcSymTable = funcSymTable;
  }

  public int getStackOffset() {
    return stackOffset;
  }

  public void resetParamStackOffset() {
    stackOffset = 0;
  }

  public void resetParamStackOffset(int size) {
    stackOffset = size;
  }

  public int getVarSize() {
    return varSize;
  }

  public void incrementVarSize(int size) {
    varSize += size;
  }
}