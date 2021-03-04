package InternalRepresentation;

import static InternalRepresentation.Enums.BranchOperation.*;
import static InternalRepresentation.Enums.BuiltInFunction.*;
import static InternalRepresentation.Enums.ConditionCode.*;
import static InternalRepresentation.Enums.LdrType.LDR;
import static InternalRepresentation.Enums.Register.*;
import static InternalRepresentation.Enums.ArithmeticOperation.*;
import static InternalRepresentation.Enums.SystemBuiltInFunction.*;

import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Instructions.*;

import java.util.ArrayList;
import java.util.List;

public class CustomBuiltInFunctions {

  private static final int FALSE = 0;

  public List<Instruction> generateAssembly(BuiltInFunction type) {
    List<Instruction> instructions = new ArrayList<>();
    instructions.add(new CustomBuiltInInstruction(type));

    switch (type) {
      case OVERFLOW:
        generateOverflow(instructions);
        break;
      case RUNTIME:
        generateRuntime(instructions);
        break;
      case ARRAY_BOUNDS:
        generateArrayBounds(instructions);
        break;
      case DIV_ZERO:
        generateDivZero(instructions);
        break;
      case NULL_POINTER:
        generateNullPointer(instructions);
        break;
      case FREE_PAIR:
        generateFreePair(instructions);
        break;
      case READ_CHAR:
        generateReadChar(instructions);
        break;
      case READ_INT:
        generateReadInt(instructions);
        break;
      case PRINT_STRING:
      case PRINT_INT:
      case PRINT_BOOL:
      case PRINT_REFERENCE:
      case PRINT_LN:
        generatePrint(type, instructions);
    }

    return instructions;
  }

  private void generatePrint(BuiltInFunction type, List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));

    switch (type) {
      case PRINT_STRING:
        generatePrintString(instructions);
        break;
      case PRINT_INT:
        generatePrintInt(instructions);
        break;
      case PRINT_BOOL:
        generatePrintBool(instructions);
        break;
      case PRINT_REFERENCE:
        generatePrintReference(instructions);
        break;
      case PRINT_LN:
        generatePrintLn(instructions);
        break;
      default:
        throw new IllegalStateException("Unexpected non-print BuiltInFunction: " + type);
    }

    instructions.add(new MovInstruction(R0, 0));
    instructions.add(new BranchInstruction(BL, FFLUSH.getMessage()));
    instructions.add(new PopInstruction(PC));
  }

  private void generateOverflow(List<Instruction> instructions) {
    instructions.add(new LdrInstruction(
        LDR, R0, new MsgInstruction("OverflowError: the result is too small/large to store in"
        + " a 4-byte signed-integer.\\n")));
    instructions.add(new BranchInstruction(BL, RUNTIME));
  }

  private void generateRuntime(List<Instruction> instructions) {
    instructions.add(new BranchInstruction(BL, PRINT_STRING));
    instructions.add(new MovInstruction(R0, -1));
    instructions.add(new BranchInstruction(BL, EXIT.getMessage()));
  }

  private void generateArrayBounds(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(R0, new Operand(0)));
    instructions.add(
        new LdrInstruction(LDR, LT, R0,
            new MsgInstruction("ArrayIndexOutOfBoundsError: negative index\\n\\0")));
    instructions.add(new BranchInstruction(LT, BL, RUNTIME));
    instructions.add(new LdrInstruction(LDR, R1, R1));
    instructions.add(new CompareInstruction(R0, new Operand(R1)));
    instructions.add(new LdrInstruction(LDR, CS, R0,
        new MsgInstruction("ArrayIndexOutOfBoundsError: index too large\\n\\0")));
    instructions.add(new BranchInstruction(CS, BL, RUNTIME));
    instructions.add(new PopInstruction(PC));
  }

  private void generateDivZero(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(R1, new Operand(0)));
    instructions.add(new LdrInstruction(LDR, EQ, R0,
        new MsgInstruction("DivideByZeroError: divide or modulo by zero\n")));
    instructions.add(new BranchInstruction(EQ, BL, RUNTIME));
    instructions.add(new PopInstruction(PC));
  }

  private void generateNullPointer(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(R0, new Operand(0)));
    instructions.add(new LdrInstruction(LDR, EQ, R0,
        new MsgInstruction("NullReferenceError: dereference a null reference\\n\\0")));
    instructions.add(new BranchInstruction(EQ, BL, RUNTIME));
    instructions.add(new PopInstruction(PC));
  }

  private void generateFreePair(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(R0, new Operand(0)));
    instructions.add(
        new LdrInstruction(LDR, EQ, R0,
            new MsgInstruction("NullReferenceError: dereference a null reference\\n\\0")));
    instructions.add(new BranchInstruction(EQ, B, RUNTIME));
    instructions.add(new PushInstruction(R0));
    instructions.add(new LdrInstruction(LDR, R0, R0));
    instructions.add(new BranchInstruction(BL, FREE.getMessage()));
    instructions.add(new LdrInstruction(LDR, R0, SP));
    instructions.add(new LdrInstruction(LDR, R0, R0, 4));
    instructions.add(new BranchInstruction(BL, FREE.getMessage()));
    instructions.add(new PopInstruction(R0));
    instructions.add(new BranchInstruction(BL, FREE.getMessage()));
    instructions.add(new PopInstruction(PC));
  }

  private void generateReadChar(List<Instruction> instructions) {
    generateRead(instructions, READ_CHAR);
  }

  private void generateReadInt(List<Instruction> instructions) {
    generateRead(instructions, READ_INT);
  }

  private void generateRead(List<Instruction> instructions, BuiltInFunction type) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new MovInstruction(R1, R0));
    switch (type) {
      case READ_CHAR:
        instructions.add(new LdrInstruction(LDR, R0, new MsgInstruction(" %c\\0")));
        break;
      case READ_INT:
        instructions.add(new LdrInstruction(LDR, R0, new MsgInstruction("%d\\0")));
        break;
      default:
        throw new IllegalStateException("Unexpected non-read BuiltInFunction: " + type);
    }

    instructions.add(new ArithmeticInstruction(ADD, R0, R0, new Operand(4), false));
    instructions.add(new BranchInstruction(BL, SCANF.getMessage()));
    instructions.add(new PopInstruction(PC));
  }

  private void generatePrintString(List<Instruction> instructions) {
    instructions.add(new LdrInstruction(LDR, R1, R0));
    instructions.add(new ArithmeticInstruction(ADD, R2, R0, new Operand(4), false));
    instructions.add(new LdrInstruction(LDR, R0, new MsgInstruction("%.*s\\0")));
    instructions.add(new ArithmeticInstruction(ADD, R0, R0, new Operand(4), false));
    instructions.add(new BranchInstruction(BL, PRINTF.getMessage()));
  }

  private void generatePrintInt(List<Instruction> instructions) {
    instructions.add(new MovInstruction(R1, R0));
    instructions.add(new LdrInstruction(LDR, R0, new MsgInstruction("%d\\0")));
    instructions.add(new ArithmeticInstruction(ADD, R0, R0, new Operand(4), false));
    instructions.add(new BranchInstruction(BL, PRINTF.getMessage()));
  }

  private void generatePrintBool(List<Instruction> instructions) {
    instructions.add(new CompareInstruction(R0, new Operand(FALSE)));
    instructions.add(new LdrInstruction(LDR, NE, R0, new MsgInstruction("true\\0")));
    instructions.add(new LdrInstruction(LDR, EQ, R0, new MsgInstruction("false\\0")));
    instructions.add(new ArithmeticInstruction(ADD, R0, R0, new Operand(4), false));
    instructions.add(new BranchInstruction(BL, PRINTF.getMessage()));
  }

  /* Also has:
        msg_0:
          .word 4
          .ascii	" = ("
        msg_1:
          .word 2
          .ascii	", "
      but idk when these are for
  */
  private void generatePrintReference(List<Instruction> instructions) {
    instructions.add(new MovInstruction(R1, R0));
    instructions.add(new LdrInstruction(LDR, R0, new MsgInstruction("%p\\0")));
    instructions.add(new ArithmeticInstruction(ADD, R0, R0, new Operand(4), false));
    instructions.add(new BranchInstruction(BL, PRINTF.getMessage()));

  }

  private void generatePrintLn(List<Instruction> instructions) {
    instructions.add(new LdrInstruction(LDR, R0, new MsgInstruction("\\0")));
    instructions.add(new ArithmeticInstruction(ADD, R0, R0, new Operand(4), false));
    instructions.add(new BranchInstruction(BL, PUTS.getMessage()));
  }
}
