package SemanticAnalysis;

import static SemanticAnalysis.DataTypes.BaseType.Type.*;

import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.BaseType.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Operator {

  public enum UnOp {
    NOT("!", BOOL, BOOL),
    NEGATION("-", INT, INT),
    LEN("len", /* ARRAY,*/ INT),
    ORD("ord", CHAR, INT),
    CHR("chr", INT, CHAR);

    private final String label;
    private final Type returnType;
    private final Set<Type> argTypes = new HashSet<>();

    UnOp(String label, Type returnType, Type... argTypes) {
      this.label = label;
      this.returnType = returnType;
      Collections.addAll(this.argTypes, argTypes);
    }

    public Type getReturnType() {
      return returnType;
    }

    public Set<Type> getArgTypes() {
      return argTypes;
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
    private final Set<DataTypeId> argTypes = new HashSet<>();

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

    public Set<DataTypeId> getArgTypes() {
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
