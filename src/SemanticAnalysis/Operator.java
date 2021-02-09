package SemanticAnalysis;

public class Operator {

  public enum UnOp {
    NOT("!"),
    NEGATION("-"),
    LENGTH("len"),
    ORD("ord"),
    CHR("chr");

    private final String label;

    UnOp(String label) {
      this.label = label;
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
    MUL("*"),
    DIV("/"),
    MOD("%"),
    PLUS("+"),
    MINUS("-"),
    GREATER(">"),
    GREATEREQ(">="),
    LESS("<"),
    LESSEQ("<="),
    EQUAL("=="),
    NOTEQUAL("!="),
    AND("&&"),
    OR("||");

    private final String label;

    BinOp(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
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
