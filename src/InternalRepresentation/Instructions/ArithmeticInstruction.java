package InternalRepresentation.Instructions;

import InternalRepresentation.Enums.ArithmeticOperation;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.InstructionPrinter;
import InternalRepresentation.Operand;

public class ArithmeticInstruction implements Instruction {

    private final InstructionPrinter printer = new InstructionPrinter();
    private final ArithmeticOperation operation;
    private final Register destReg;
    private final Register operand1;
    private final Operand operand2;
    private final boolean setBits;

    public ArithmeticInstruction(ArithmeticOperation operation, Register destReg,
        Register operand1, Operand operand2, boolean setBits) {
        this.operation = operation;

        this.destReg = destReg;
        this.operand1 = operand1;
        this.operand2 = operand2;

        this.setBits = setBits;
    }

    @Override
    public String writeInstruction() {
        return printer.printArithmetic(operation, destReg, operand1, operand2, setBits);
    }

}