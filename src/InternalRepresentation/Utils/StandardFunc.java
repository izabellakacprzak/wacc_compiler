package InternalRepresentation.Utils;

import static InternalRepresentation.Instructions.DirectiveInstruction.Directive.LTORG;
import static InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn.*;
import static SemanticAnalysis.DataTypes.BaseType.Type.*;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public enum StandardFunc {
  FILL_INT("fill_int", new ArrayType(new BaseType(INT)),
      new BaseType(INT), new ArrayType(new BaseType(INT))),
  FILL_CHAR("fill_char", new ArrayType(new BaseType(CHAR)),
      new BaseType(CHAR), new ArrayType(new BaseType(CHAR))),
  INDEX_OF_INT("index_of_int", new BaseType(INT),
      new BaseType(INT), new ArrayType(new BaseType(INT))),
  INDEX_OF_CHAR("index_of_char", new BaseType(INT),
      new BaseType(CHAR), new ArrayType(new BaseType(CHAR))),
  MAX("max", new BaseType(INT),
      new ArrayType(new BaseType(INT))),
  MIN("min", new BaseType(INT),
      new ArrayType(new BaseType(INT))),
  SWAP_INT("swap_int", new ArrayType(new BaseType(INT)),
      new BaseType(INT), new BaseType(INT), new ArrayType(new BaseType(INT))),
  SWAP_CHAR("swap_char", new ArrayType(new BaseType(CHAR)),
      new BaseType(INT), new BaseType(INT), new ArrayType(new BaseType(CHAR))),
  IS_SORTED("sorted", new BaseType(BOOL),
      new ArrayType(new BaseType(INT))),
  MIN_INDEX_FROM("min_index_from", new BaseType(INT),
      new ArrayType(new BaseType(INT)), new BaseType(INT)),
  SORT("sort", new ArrayType(new BaseType(INT)),
      new ArrayType(new BaseType(INT))),
  STRCMP("strcmp", new BaseType(INT),
      new ArrayType(new BaseType(CHAR)), new ArrayType(new BaseType(CHAR))),
  CONTAINS_INT("contains_int", new BaseType(BOOL),
      new BaseType(INT), new ArrayType(new BaseType(INT))),
  CONTAINS_CHAR("contains_char", new BaseType(BOOL),
      new BaseType(CHAR), new ArrayType(new BaseType(CHAR))),
  CONTAINS_BOOL("contains_bool", new BaseType(BOOL),
      new BaseType(BOOL), new ArrayType(new BaseType(BOOL))),


  // TODO: these don't work because of msg calls in the assembly
  /* PRINT_INT_ARRAY("print_int_array", new BaseType(BOOL), new ArrayType(new BaseType(INT))),
  PRINT_CHAR_ARRAY("print_char_array", new BaseType(BOOL), new ArrayType(new BaseType(CHAR)))*/;

  private static final String STD_LIBRARY = "src/lib/stdLib.s";
  private static final String END_OF_FUNCTION = "." + LTORG.toString().toLowerCase();

  private final String label;
  private final DataTypeId returnType;
  private final List<DataTypeId> argTypes;

  private boolean used = false;

  StandardFunc(String label, DataTypeId returnType, DataTypeId... argTypes) {
    this.label = label;
    this.returnType = returnType;
    this.argTypes = List.of(argTypes);
  }

  /* Returns all library functions used in the program */
  public static List<StandardFunc> getUsed() {
    return Arrays.stream(StandardFunc.values()).filter(StandardFunc::isUsed)
               .collect(Collectors.toList());
  }

  /* Method for finding a StandardFunc enum from its label */
  public static StandardFunc valueOfLabel(String label) {
    for (StandardFunc op : values()) {
      if (op.label.equals(label)) {
        return op;
      }
    }
    return null;
  }

  public String getBranchLabel() {
    return "l_" + label;
  }

  public DataTypeId getReturnType() {
    return returnType;
  }

  public List<DataTypeId> getArgTypes() {
    return argTypes;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed() {
    this.used = true;

    switch (this) {
      // TODO: for if the prints start working
      /*
      case PRINT_INT_ARRAY:
        PRINT_INT.setUsed();
      case PRINT_CHAR_ARRAY:
        PRINT_STRING.setUsed();
        PRINT_LN.setUsed();
       */
      case FILL_INT:
      case FILL_CHAR:
      case INDEX_OF_INT:
      case INDEX_OF_CHAR:
      case MAX:
      case MIN:
      case IS_SORTED:
      case MIN_INDEX_FROM:
      case STRCMP:
      case CONTAINS_INT:
      case CONTAINS_CHAR:
      case CONTAINS_BOOL:
        OVERFLOW.setUsed();
      case SWAP_INT:
      case SWAP_CHAR:
        ARRAY_BOUNDS.setUsed();
        break;
      case SORT:
        MIN_INDEX_FROM.setUsed();
        SWAP_INT.setUsed();
        OVERFLOW.setUsed();
    }
  }

  public void writeAssembly(FileWriter writer) {
    File file = new File(STD_LIBRARY);

    try {
      Scanner scanner = new Scanner(file);

      String line = "";
      while (scanner.hasNextLine()) {
        line = scanner.nextLine();
        if (line.trim().equals(getBranchLabel() + ":")) {
          break;
        }
      }

      writer.write(line);
      writer.write("\n");

      while (scanner.hasNextLine()) {
        line = scanner.nextLine();
        writer.write(line);
        writer.write("\n");
        if (line.trim().equals(END_OF_FUNCTION)) {
          break;
        }
      }

    } catch (Exception ignored) {
      // TODO: any way of handling the exception?
    }
  }
}
