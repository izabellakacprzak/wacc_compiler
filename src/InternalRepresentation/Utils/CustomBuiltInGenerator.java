package InternalRepresentation.Utils;

import static InternalRepresentation.Instructions.BranchInstruction.BranchOperation.*;
import static InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn.*;
import static InternalRepresentation.Utils.ConditionCode.*;
import static InternalRepresentation.Instructions.LdrInstruction.LdrType.LDR;
import static InternalRepresentation.Utils.Register.*;
import static InternalRepresentation.Instructions.ArithmeticInstruction.ArithmeticOperation.*;
import static InternalRepresentation.Utils.BuiltInFunction.SystemBuiltIn.*;

import InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn;
import InternalRepresentation.Instructions.*;

import java.util.ArrayList;
import java.util.List;

public class CustomBuiltInGenerator {

  private static final int FALSE = 0;

  private CustomBuiltInGenerator() {
  }

  public static List<Instruction> generateAssembly(CustomBuiltIn type) {
    List<Instruction> instructions = new ArrayList<>();
    instructions.add(new LabelInstruction(type.getLabel()));

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
        generateRead(instructions, READ_CHAR);
        break;
      case READ_INT:
        generateRead(instructions, READ_INT);
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

  private static void generatePrint(CustomBuiltIn type, List<Instruction> instructions) {
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

    instructions.add(new MovInstruction(Register.DEST_REG, 0));
    instructions.add(new BranchInstruction(BL, FFLUSH.getLabel()));
    instructions.add(new PopInstruction(PC));
  }

  private static void generateOverflow(List<Instruction> instructions) {
    instructions.add(new LdrInstruction(
        LDR, Register.DEST_REG,
        new MsgInstruction("OverflowError: the result is too small/large to store in"
            + " a 4-byte signed-integer.\\n")));
    instructions.add(new BranchInstruction(BL, RUNTIME));
  }

  private static void generateRuntime(List<Instruction> instructions) {
    instructions.add(new BranchInstruction(BL, PRINT_STRING));
    instructions.add(new MovInstruction(Register.DEST_REG, -1));
    instructions.add(new BranchInstruction(BL, EXIT.getLabel()));
  }

  private static void generateArrayBounds(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(Register.DEST_REG, new Operand(0)));
    instructions.add(
        new LdrInstruction(LDR, LT, Register.DEST_REG,
            new MsgInstruction("ArrayIndexOutOfBoundsError: negative index\\n\\0")));
    instructions.add(new BranchInstruction(LT, BL, RUNTIME));
    instructions.add(new LdrInstruction(LDR, Register.ARG_REG_1, Register.ARG_REG_1));
    instructions.add(new CompareInstruction(Register.DEST_REG, new Operand(Register.ARG_REG_1)));
    instructions.add(new LdrInstruction(LDR, CS, Register.DEST_REG,
        new MsgInstruction("ArrayIndexOutOfBoundsError: index too large\\n\\0")));
    instructions.add(new BranchInstruction(CS, BL, RUNTIME));
    instructions.add(new PopInstruction(PC));
  }

  private static void generateDivZero(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(Register.ARG_REG_1, new Operand(0)));
    instructions.add(new LdrInstruction(LDR, EQ, Register.DEST_REG,
        new MsgInstruction("DivideByZeroError: divide or modulo by zero\\n\\0")));
    instructions.add(new BranchInstruction(EQ, BL, RUNTIME));
    instructions.add(new PopInstruction(PC));
  }

  private static void generateNullPointer(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(Register.DEST_REG, new Operand(0)));
    instructions.add(new LdrInstruction(LDR, EQ, Register.DEST_REG,
        new MsgInstruction("NullReferenceError: dereference a null reference\\n\\0")));
    instructions.add(new BranchInstruction(EQ, BL, RUNTIME));
    instructions.add(new PopInstruction(PC));
  }

  private static void generateFreePair(List<Instruction> instructions) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new CompareInstruction(Register.DEST_REG, new Operand(0)));
    instructions.add(
        new LdrInstruction(LDR, EQ, Register.DEST_REG,
            new MsgInstruction("NullReferenceError: dereference a null reference\\n\\0")));
    instructions.add(new BranchInstruction(EQ, B, RUNTIME));
    instructions.add(new PushInstruction(Register.DEST_REG));
    instructions.add(new LdrInstruction(LDR, Register.DEST_REG, Register.DEST_REG));
    instructions.add(new BranchInstruction(BL, FREE.getLabel()));
    instructions.add(new LdrInstruction(LDR, Register.DEST_REG, SP));
    instructions.add(new LdrInstruction(LDR, Register.DEST_REG, Register.DEST_REG, 4));
    instructions.add(new BranchInstruction(BL, FREE.getLabel()));
    instructions.add(new PopInstruction(Register.DEST_REG));
    instructions.add(new BranchInstruction(BL, FREE.getLabel()));
    instructions.add(new PopInstruction(PC));
  }

  private static void generateRead(List<Instruction> instructions, CustomBuiltIn type) {
    instructions.add(new PushInstruction(LR));
    instructions.add(new MovInstruction(Register.ARG_REG_1, Register.DEST_REG));
    switch (type) {
      case READ_CHAR:
        instructions.add(new LdrInstruction(LDR, Register.DEST_REG, new MsgInstruction(" %c\\0")));
        break;
      case READ_INT:
        instructions.add(new LdrInstruction(LDR, Register.DEST_REG, new MsgInstruction("%d\\0")));
        break;
      default:
        throw new IllegalStateException("Unexpected non-read BuiltInFunction: " + type);
    }

    instructions.add(
        new ArithmeticInstruction(
            ADD, Register.DEST_REG, Register.DEST_REG, new Operand(4), false));
    instructions.add(new BranchInstruction(BL, SCANF.getLabel()));
    instructions.add(new PopInstruction(PC));
  }

  private static void generatePrintString(List<Instruction> instructions) {
    instructions.add(new LdrInstruction(LDR, Register.ARG_REG_1, Register.DEST_REG));
    instructions.add(
        new ArithmeticInstruction(ADD, Register.ARG_REG_2, Register.DEST_REG, new Operand(4),
            false));
    instructions.add(new LdrInstruction(LDR, Register.DEST_REG, new MsgInstruction("%.*s\\0")));
    instructions.add(
        new ArithmeticInstruction(ADD, Register.DEST_REG, Register.DEST_REG, new Operand(4),
            false));
    instructions.add(new BranchInstruction(BL, PRINTF.getLabel()));
  }

  private static void generatePrintInt(List<Instruction> instructions) {
    instructions.add(new MovInstruction(Register.ARG_REG_1, Register.DEST_REG));
    instructions.add(new LdrInstruction(LDR, Register.DEST_REG, new MsgInstruction("%d\\0")));
    instructions.add(
        new ArithmeticInstruction(ADD, Register.DEST_REG, Register.DEST_REG, new Operand(4),
            false));
    instructions.add(new BranchInstruction(BL, PRINTF.getLabel()));
  }

  private static void generatePrintBool(List<Instruction> instructions) {
    instructions.add(new CompareInstruction(Register.DEST_REG, new Operand(FALSE)));
    instructions.add(new LdrInstruction(LDR, NE, Register.DEST_REG, new MsgInstruction("true\\0")));
    instructions
        .add(new LdrInstruction(LDR, EQ, Register.DEST_REG, new MsgInstruction("false\\0")));
    instructions.add(
        new ArithmeticInstruction(ADD, Register.DEST_REG, Register.DEST_REG, new Operand(4),
            false));
    instructions.add(new BranchInstruction(BL, PRINTF.getLabel()));
  }

  private static void generatePrintReference(List<Instruction> instructions) {
    instructions.add(new MovInstruction(Register.ARG_REG_1, Register.DEST_REG));
    instructions.add(new LdrInstruction(LDR, Register.DEST_REG, new MsgInstruction("%p\\0")));
    instructions.add(
        new ArithmeticInstruction(ADD, Register.DEST_REG, Register.DEST_REG, new Operand(4),
            false));
    instructions.add(new BranchInstruction(BL, PRINTF.getLabel()));

  }

  private static void generatePrintLn(List<Instruction> instructions) {
    instructions.add(new LdrInstruction(LDR, Register.DEST_REG, new MsgInstruction("\\0")));
    instructions.add(
        new ArithmeticInstruction(ADD, Register.DEST_REG, Register.DEST_REG, new Operand(4),
            false));
    instructions.add(new BranchInstruction(BL, PUTS.getLabel()));
  }
}
