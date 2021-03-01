package InternalRepresentation.Enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BuiltInFunction {
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
