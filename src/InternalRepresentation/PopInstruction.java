package InternalRepresentation;

public class PopInstruction implements Instruction{

    // General Pop instruction takes in a reglist but could not see such an example
    // in the WACC arm output
    private final InstructionPrinter printer = new InstructionPrinter();
    private final Register reg;

    public PopInstruction(Register reg) {
        this.reg = reg;
    }

    @Override
    public String writeInstruction() {
        return printer.printPop(reg);
    }
}