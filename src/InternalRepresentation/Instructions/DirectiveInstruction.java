package InternalRepresentation.Instructions;

import InternalRepresentation.InstructionPrinter;

public final class DirectiveInstruction implements Instruction {

  /* printer: instruction printer used for generating String representations of instructions
   * type:    type of the directive
   * value:   optional value eg. length associated with .word directive
   */
  private final InstructionPrinter printer = new InstructionPrinter();
  private final Directive type;
  private final String value;

  public DirectiveInstruction(Directive type, String value) {

    assert type != null;
    assert value != null;

    this.type = type;
    this.value = value;
  }

  public DirectiveInstruction(Directive type) {
    this(type, "");
  }

  /* Generates string representation of ARM instruction */
  @Override
  public String writeInstruction() {
    return printer.printDirective(type, value);
  }

  /* Type of directive */
  public enum Directive {LTORG, WORD, ASCII, DATA, TEXT, GLOBAL}
}
