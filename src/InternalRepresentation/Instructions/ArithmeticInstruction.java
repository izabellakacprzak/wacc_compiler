package InternalRepresentation.Instructions;

import static InternalRepresentation.Utils.Register.*;
import InternalRepresentation.Utils.Register;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Utils.Operand;

public final class ArithmeticInstruction implements Instruction {

    /* printer:     instruction printer used for generating String representations of instructions
     * operation:   type of arithmetic operation
     * destReg:     destination register where the result should be stored
     * operand1:    left operand of the arithmetic operation
     * operand2:    right operand of the arithmetic operation
     * setBits:     true if condition flags should be set false otherwise
     */
    private final InstructionPrinter printer = new InstructionPrinter();
    private final ArithmeticOperation operation;
    private final Register destReg;
    private final Register operand1;
    private final Operand operand2;
    private final boolean setBits;

    public ArithmeticInstruction(ArithmeticOperation operation, Register destReg,
        Register operand1, Operand operand2, boolean setBits) {

        assert destReg != null;
        assert operand1 != null;
        assert operand2 != null;

        this.operation = operation;
        this.destReg = destReg;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.setBits = setBits;
    }

    /* Generates string representation of ARM instruction */
    @Override
    public String writeInstruction() {
        return printer.printArithmetic(operation, destReg, operand1, operand2, setBits);
    }

    /* Type of arithmetic operation */
    public enum ArithmeticOperation {ADD, ADC, SUB, SBC, RSB, RSC}
}