package InternalRepresentation.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BuiltInFunction {

  /* Builtin system functions */
  public enum SystemBuiltIn {
    MALLOC("malloc"),
    PRINTF("printf"),
    SCANF("scanf"),
    IDIV("__aeabi_idiv"),
    IDIVMOD("__aeabi_idivmod"),
    FFLUSH("fflush"),
    PUTS("puts"),
    EXIT("exit"),
    FREE("free"),
    PUTCHAR("putchar");

    /* label: string label of builtin function */
    private final String label;

    SystemBuiltIn(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  /* Custom builtin functions */
  public enum CustomBuiltIn {
    OVERFLOW("p_throw_overflow_error"),
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

    /* label: string label of builtin function
     * used:  true if function is accessed in the program false otherwise
     */
    private final String label;
    private boolean used;

    CustomBuiltIn(String label) {
      this.label = label;
      this.used = false;
    }

    /* Returns all custom builtin functions used in the program */
    public static List<CustomBuiltIn> getUsed() {
      return Arrays.stream(CustomBuiltIn.values()).filter(CustomBuiltIn::isUsed)
          .collect(Collectors.toList());
    }

    public boolean isUsed() {
      return used;
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
  }
}
