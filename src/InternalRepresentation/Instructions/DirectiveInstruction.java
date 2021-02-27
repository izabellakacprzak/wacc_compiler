package InternalRepresentation.Instructions;

public class DirectiveInstruction implements Instruction {

  private final Directive type;

  public DirectiveInstruction(Directive type) {
    this.type = type;
  }

  @Override
  public String writeInstruction() {
    return "." + type.name().toLowerCase();
  }

  public enum Directive {LTORG}
}
