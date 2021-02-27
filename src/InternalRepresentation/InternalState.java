package InternalRepresentation;

import InternalRepresentation.Instructions.Instruction;
import InternalRepresentation.Instructions.LabelInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InternalState {

    private Stack<Register> stack;
    private int labelCount;
    private final List<String> declaredLabels;
    private final List<Instruction> generatedInstructions;

    public InternalState() {
        // setup stack function
        declaredLabels = new ArrayList<>();
        generatedInstructions = new ArrayList<>();

        labelCount = 0;
    }

    public Register getFreeRegister() {
        return null;
    }

    public void addInstruction(Instruction instruction) {
        generatedInstructions.add(instruction);
    }

    public String generateNewLabel() {
        String newLabel = "L" + labelCount;
        declaredLabels.add(newLabel);
        labelCount++;
        return newLabel;
    }
}