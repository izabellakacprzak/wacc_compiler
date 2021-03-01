package InternalRepresentation.Instructions;

public class BuiltInLabelInstruction extends LabelInstruction {

  private final SystemBuiltInFunction function;

  public BuiltInLabelInstruction(SystemBuiltInFunction function) {
    super(function.getMessage());
    this.function = function;
  }

}
