package AbstractSyntaxTree.expression;

import static InternalRepresentation.Enums.ArithmeticOperation.*;
import static InternalRepresentation.Enums.BranchOperation.*;
import static InternalRepresentation.Enums.Register.*;
import static InternalRepresentation.Enums.LdrType.*;
import static InternalRepresentation.Enums.BuiltInFunction.*;
import static InternalRepresentation.Enums.ShiftType.LSL;

import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.ArithmeticInstruction;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.InternalState;
import InternalRepresentation.Operand;
import InternalRepresentation.Shift;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ArrayElemNode extends ExpressionNode {

  /* identifier:  IdentifierNode representing the identifier of this node
   * expressions: List of ExpressionNodes corresponding to the INT references to an array element */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> expressions;
  private SymbolTable currSymTable = null;

  public ArrayElemNode(int line, int charPositionInLine, IdentifierNode identifier,
                       List<ExpressionNode> expressions) {
    super(line, charPositionInLine);
    this.identifier = identifier;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on stored nodes */
    identifier.semanticAnalysis(symbolTable, errorMessages);

    for (ExpressionNode expression : expressions) {
      expression.semanticAnalysis(symbolTable, errorMessages);
    }

    /* Check identifier has been declared and is of an ARRAY type */
    Identifier idType = symbolTable.lookupAll(identifier.getIdentifier());

    if (idType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " No declaration of '" + identifier.getIdentifier() + "' identifier."
                            + " Expected: ARRAY IDENTIFIER.");
      return;
    }

    if (!(identifier.getType(symbolTable) instanceof ArrayType)) {
      System.out.println(identifier);
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Incompatible type of '" + identifier.getIdentifier() + "' identifier."
                            + " Expected: ARRAY IDENTIFIER Actual: " + idType);
      return;
    }

    /* Check that each expression is of type INT */
    DataTypeId thisType;
    for (ExpressionNode expression : expressions) {
      thisType = expression.getType(symbolTable);

      if (thisType == null) {
        errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
                              + " Could not resolve type of '" + expression + "' in ARRAY ELEM."
                              + " Expected: INT");
        break;
      }

      if (!thisType.equals(new BaseType(BaseType.Type.INT))) {
        errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
                              + " Incompatible type of '" + expression + "' in ARRAY ELEM."
                              + " Expected: INT Actual: " + thisType);
      }
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    // get available register
    Register arrayReg = internalState.popFreeRegister();

    generateElemAddr(internalState, arrayReg);
    internalState.addInstruction(new LdrInstruction(LDR, arrayReg, arrayReg));

    internalState.pushFreeRegister(arrayReg);
  }

  public void generateElemAddr(InternalState internalState, Register arrayReg) {
    // put address of array into register
    int offset = currSymTable.getOffset(identifier.getIdentifier());
    internalState
        .addInstruction(new ArithmeticInstruction(ADD, arrayReg, SP, new Operand(offset), false));

    // evaluate each index expression
    for (ExpressionNode expression : expressions) {
      expression.generateAssembly(internalState);
      Register exprReg = internalState.peekFreeRegister();
      internalState.addInstruction(new LdrInstruction(LDR, arrayReg, arrayReg));
      // move result of expression to R0
      internalState.addInstruction(new MovInstruction(R0, exprReg));
      // move result of array to R1
      internalState.addInstruction(new MovInstruction(R1, arrayReg));
      internalState.addInstruction(new BranchInstruction(BL, ARRAY_BOUNDS));
      internalState.addInstruction(new ArithmeticInstruction(ADD, arrayReg, arrayReg,
          new Operand(INT_BYTES_SIZE), false));
      internalState.addInstruction(new ArithmeticInstruction(ADD, arrayReg, arrayReg,
          new Operand(exprReg, new Shift(LSL, 2)), false));
    }
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  /* Return the type of the elements stored in identifier array */
  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    DataTypeId idType = identifier.getType(symbolTable);
    if (!(idType instanceof ArrayType)) {
      return null;
    }

    return ((ArrayType) idType).getElemType();
  }

  /* Returns a ArrayElem in the form: array_id[expr1][expr2]...[exprN] */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();

    str.append(identifier.getIdentifier()).append("[");

    for (ExpressionNode expression : expressions) {
      str.append(expression).append("][");
    }

    str.deleteCharAt(str.length() - 1);

    return str.toString();
  }
}
