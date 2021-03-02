package InternalRepresentation.Instructions;


import InternalRepresentation.Enums.Directive;
import java.util.ArrayList;
import java.util.List;

public class MsgInstruction implements Instruction{
  private static final List<MsgInstruction> messages = new ArrayList<>();

  private final DirectiveInstruction length;
  private final DirectiveInstruction value;
  private int index;

  public MsgInstruction(int length, String value) {
    this.length = new DirectiveInstruction(Directive.WORD, String.valueOf(length));
    this.value = new DirectiveInstruction(Directive.ASCII, value);
    this.index = messages.size();
    MsgInstruction.messages.add(this);
  }

  public MsgInstruction(String value) {
    this(value.length(), value);
  }

  public static List<MsgInstruction> getMessages() {
    return messages;
  }

  @Override
  public String writeInstruction() {
    return length.writeInstruction() + "\n" + value.writeInstruction();
  }

  @Override
  public String toString() {
    return "msg_" + index;
  }

}
