package InternalRepresentation.Instructions;

public class BuiltInLabelInstruction extends LabelInstruction {

  private final SystemBuiltInFunction function;

  public BuiltInLabelInstruction(SystemBuiltInFunction function) {
    super(function.getMessage());
    this.function = function;
  }

  public enum SystemBuiltInFunction {
    MALLOC("malloc"),
    PRINTF("printf"),
    IDIV("__aeabi_idiv"),
    IDIVMOD("__aeabi_idivmod");

    private final String message;

    SystemBuiltInFunction(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
