package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.BuiltInFunction;

public class CustomBuiltInInstruction extends LabelInstruction {

  private final BuiltInFunction function;

  public CustomBuiltInInstruction(BuiltInFunction function) {
    super(function.getLabel());
    this.function = function;
  }

}
