package InternalRepresentation.Instructions;


import InternalRepresentation.Enums.Directive;
import java.util.ArrayList;
import java.util.List;

public final class MsgInstruction implements Instruction {

  private static final List<MsgInstruction> messages = new ArrayList<>();

  private final DirectiveInstruction length;
  private final DirectiveInstruction value;
  private final int index;

  public MsgInstruction(String value) {
    int length = value.replace("\\0", "\0").replace("\\n", "\n").length();
    this.length = new DirectiveInstruction(Directive.WORD, String.valueOf(length));
    this.value = new DirectiveInstruction(Directive.ASCII, value);
    this.index = messages.size();
    MsgInstruction.messages.add(this);
  }

  public static List<MsgInstruction> getMessages() {
    return messages;
  }

  @Override
  public String writeInstruction() {
    return length.writeInstruction() + "\n\t" + value.writeInstruction();
  }

  @Override
  public String toString() {
    return "msg_" + index;
  }

}
