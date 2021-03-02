package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.SystemBuiltInFunction;

public class BuiltInLabelInstruction extends LabelInstruction {

  private final SystemBuiltInFunction function;

  public BuiltInLabelInstruction(SystemBuiltInFunction function) {
    super(function.getMessage());
    this.function = function;
  }

}
