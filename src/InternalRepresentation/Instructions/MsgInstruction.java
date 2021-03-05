package InternalRepresentation.Instructions;

import InternalRepresentation.Instructions.DirectiveInstruction.Directive;
import java.util.ArrayList;
import java.util.List;

public final class MsgInstruction implements Instruction {

  /* List of all generated messages in the program */
  private static final List<MsgInstruction> messages = new ArrayList<>();

  /* length:  length of message (\0 included)
   * value:   string value of message
   * index:   index of message inside the list of messages
   */
  private final DirectiveInstruction length;
  private final DirectiveInstruction value;
  private final int index;

  public MsgInstruction(String value) {
    int length = value.replace("\\0", "\0").
            replace("\\n", "\n").
            replace("\\\"", "\"").
            replace("\\'", "'").length();
    this.length = new DirectiveInstruction(Directive.WORD, String.valueOf(length));
    this.value = new DirectiveInstruction(Directive.ASCII, value);
    this.index = messages.size();
    MsgInstruction.messages.add(this);
  }

  public static List<MsgInstruction> getMessages() {
    return messages;
  }

  /* Generates string representation of ARM instruction */
  @Override
  public String writeInstruction() {
    return length.writeInstruction() + "\n\t" + value.writeInstruction();
  }

  /* Generates message label fo the form: msg_0 */
  @Override
  public String toString() {
    return "msg_" + index;
  }

}
