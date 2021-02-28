package InternalRepresentation.Instructions;

public class DirectiveInstruction implements Instruction {

  private final Directive type;
  private final String value;

  public DirectiveInstruction(Directive type, String value) {
    this.type = type;
    this.value = value;
  }

  public DirectiveInstruction(Directive type) {
    this.type = type;
    this.value = null;
  }

  @Override
  public String writeInstruction() {
    return "." + type.name().toLowerCase();
  }

  public enum Directive {LTORG, WORD, ASCII}
}
