package SemanticAnalysis;

import static SemanticAnalysis.DataTypes.BaseType.Type.BOOL;
import static SemanticAnalysis.DataTypes.BaseType.Type.CHAR;
import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.BaseType.Type;
import java.util.ArrayList;
import java.util.List;

public class Operator {

  public enum UnOp {
    NOT("!", BOOL, BOOL),
    NEGATION("-", INT, INT),
    LEN("len", INT),
    ORD("ord", INT, CHAR),
    CHR("chr", CHAR, INT);

    private final String label;
    private final DataTypeId returnType;
    private final List<DataTypeId> argTypes = new ArrayList<>();

    /* When specifying a type, no types implies an Array */
    UnOp(String label, Type returnType, Type... argTypes) {
      this.label = label;
      this.returnType = new BaseType(returnType);

      for (Type type : argTypes) {
        this.argTypes.add(new BaseType(type));
      }
    }

    public DataTypeId getReturnType() {
      return returnType;
    }

    public List<DataTypeId> getArgTypes() {
      return argTypes;
    }

    public String getLabel() {
      return label;
    }

    public static UnOp valueOfLabel(String label) {
      for (UnOp op : values()) {
        if (op.label.equals(label)) {
          return op;
        }
      }
      return null;
    }
  }

  public enum BinOp {
    MUL("*", INT, INT),
    DIV("/", INT, INT),
    MOD("%", INT, INT),
    PLUS("+", INT, INT),
    MINUS("-", INT, INT),
    GREATER(">", BOOL, INT, CHAR),
    GREATEREQ(">=", BOOL, INT, CHAR),
    LESS("<", BOOL, INT, CHAR),
    LESSEQ("<=", BOOL, INT, CHAR),
    EQUAL("==", BOOL),
    NOTEQUAL("!=", BOOL),
    AND("&&", BOOL, BOOL),
    OR("||", BOOL, BOOL);

    private final String label;
    private final DataTypeId returnType;
    private final List<DataTypeId> argTypes = new ArrayList<>();

    /* When specifying a type, no types implies any type */
    BinOp(String label, Type returnType, Type... argTypes) {
      this.label = label;
      this.returnType = new BaseType(returnType);

      for (Type type : argTypes) {
        this.argTypes.add(new BaseType(type));
      }
    }

    public String getLabel() {
      return label;
    }

    public DataTypeId getReturnType() {
      return returnType;
    }

    public List<DataTypeId> getArgTypes() {
      return argTypes;
    }

    public static BinOp valueOfLabel(String label) {
      for (BinOp op : values()) {
        if (op.label.equals(label)) {
          return op;
        }
      }
      return null;
    }
  }
}
