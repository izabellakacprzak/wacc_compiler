package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.BuiltInFunction;

public class BuiltInLabelInstruction extends LabelInstruction {

  private final BuiltInFunction function;

  public BuiltInLabelInstruction(BuiltInFunction function) {
    super(function.getLabel());
    this.function = function;
  }

  public BuiltInFunction getFunction(){
    return function;
  }

}
