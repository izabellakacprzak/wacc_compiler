package InternalRepresentation.Utils;

import static InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn.*;
import static SemanticAnalysis.DataTypes.BaseType.Type.*;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import java.util.Arrays;
import java.util.List;
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
      new ArrayType(new BaseType(INT))),
  SORT("sort", new ArrayType(new BaseType(INT)),
      new ArrayType(new BaseType(INT)));

  private final String label;
  private final DataTypeId returnType;
  private final int args;
  private final List<DataTypeId> argTypes;

  private boolean used = false;

  StandardFunc(String label, DataTypeId returnType, DataTypeId... argTypes) {
    this.label = label;
    this.returnType = returnType;
    this.args = argTypes.length;
    this.argTypes = List.of(argTypes);
  }

  public String getBranchLabel() {
    return "l_" + label;
  }

  public DataTypeId getReturnType() {
    return returnType;
  }

  public int getArgs() {
    return args;
  }

  public List<DataTypeId> getArgTypes() {
    return argTypes;
  }

  public boolean isUsed() {
    return used;
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

  public void setUsed() {
    this.used = true;

    switch (this) {
      case FILL_INT:
      case FILL_CHAR:
      case INDEX_OF_INT:
      case INDEX_OF_CHAR:
      case MAX:
      case MIN:
      case IS_SORTED:
      case MIN_INDEX_FROM:
        OVERFLOW.setUsed();
      case SWAP_INT:
      case SWAP_CHAR:
        ARRAY_BOUNDS.setUsed();
        break;
      case SORT:
        MIN_INDEX_FROM.setUsed();
        OVERFLOW.setUsed();
    }
  }
}
