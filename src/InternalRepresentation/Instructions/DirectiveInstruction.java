package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.Directive;
import InternalRepresentation.InstructionPrinter;

public final class DirectiveInstruction implements Instruction {

  private final InstructionPrinter printer = new InstructionPrinter();
  private final Directive type;
  private final String value;

  public DirectiveInstruction(Directive type, String value) {
    this.type = type;
    this.value = value;
  }

  public DirectiveInstruction(Directive type) {
    this(type, "");
  }

  //TODO: MOVE TO PRINTER
  @Override
  public String writeInstruction() {
    return printer.printDirective(type, value);
  }

}
