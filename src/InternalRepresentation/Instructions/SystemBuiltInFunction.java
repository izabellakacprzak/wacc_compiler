package InternalRepresentation.Instructions;

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
