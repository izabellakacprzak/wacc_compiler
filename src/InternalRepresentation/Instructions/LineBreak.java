package InternalRepresentation.Instructions;

public class LineBreak implements Instruction{

  @Override
  public String writeInstruction() {
    return "\n";
  }
}
