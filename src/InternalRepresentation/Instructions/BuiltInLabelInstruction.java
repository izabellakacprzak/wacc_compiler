package InternalRepresentation.Instructions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BuiltInLabelInstruction extends LabelInstruction{

  private final BuiltInFunction function;

  public BuiltInLabelInstruction(BuiltInFunction function) {
    super(function.getMessage());
    this.function = function;
  }

  public enum BuiltInFunction {
    OVERFLOW("p_throw_overflow_error");

    private final String message;
    private boolean used;

    BuiltInFunction(String message) {
      this.message = message;
      this.used = false;
    }

    public void setUsed() {
      this.used = true;
    }

    public String getMessage() {
      return message;
    }

    public static List<BuiltInFunction> getUsed() {
      return Arrays.stream(BuiltInFunction.values()).filter(c -> c.used)
          .collect(Collectors.toList());
    }
  }
}
