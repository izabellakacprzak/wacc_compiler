package InternalRepresentation.Instructions;


import InternalRepresentation.Instructions.DirectiveInstruction.Directive;

public class MsgInstruction {
  private final DirectiveInstruction length;
  private final DirectiveInstruction value;

  public MsgInstruction(int length, String value) {
    this.length = new DirectiveInstruction(Directive.WORD, String.valueOf(length));
    this.value = new DirectiveInstruction(Directive.ASCII, value);
  }
}
