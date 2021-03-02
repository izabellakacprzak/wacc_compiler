package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.Directive;

public class DirectiveInstruction implements Instruction {

  private final Directive type;
  private final String value;

  public DirectiveInstruction(Directive type, String value) {
    this.type = type;
    this.value = value;
  }

  public DirectiveInstruction(Directive type) {
    this.type = type;
    this.value = "";
  }
 //TODO: MOVE TO PRINTER
  @Override
  public String writeInstruction() {
    return "." + type.name().toLowerCase() + " " + value + "\n";
  }

}
