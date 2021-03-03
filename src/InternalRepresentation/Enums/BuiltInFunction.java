package InternalRepresentation.Enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BuiltInFunction {
  OVERFLOW(
      "p_throw_overflow_error"),
    RUNTIME("p_throw_runtime_error"),
    ARRAY_BOUNDS("p_check_array_bounds"),
    DIV_ZERO("p_check_divide_by_zero"),
    NULL_POINTER("p_check_null_pointer"),
    FREE_PAIR("p_free_pair"),
    READ_CHAR("p_read_char"),
    READ_INT("p_read_int"),
    PRINT_STRING("p_print_string"),
    PRINT_INT("p_print_int"),
    PRINT_BOOL("p_print_bool"),
    PRINT_REFERENCE("p_print_reference"),
    PRINT_LN("p_print_ln");

    private final String label;

  public boolean isUsed() {
    return used;
  }

  private boolean used;

    BuiltInFunction(String label) {
        this.label = label;
        this.used = false;
    }

    public void setUsed() {
      this.used = true;

      switch (this) {
        case OVERFLOW:
        case ARRAY_BOUNDS:
        case DIV_ZERO:
        case NULL_POINTER:
        case FREE_PAIR:
          RUNTIME.setUsed();
          break;
        case RUNTIME:
          PRINT_STRING.setUsed();
      }
    }

    public String getLabel() {
        return label;
    }

    public static List<BuiltInFunction> getUsed() {
        return Arrays.stream(BuiltInFunction.values()).filter(BuiltInFunction::isUsed)
                .collect(Collectors.toList());
    }
}
