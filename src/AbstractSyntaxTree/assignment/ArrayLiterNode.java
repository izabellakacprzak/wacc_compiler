package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.Enums.*;
import InternalRepresentation.Instructions.*;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.SymbolTable;

import java.util.List;
public class ArrayLiterNode extends AssignRHSNode {

  /* expressions: List of ExpressionNodes corresponding to each element of the ARRAY literal */
  private final List<ExpressionNode> expressions;
  private SymbolTable currSymTable = null;


  public ArrayLiterNode(int line, int charPositionInLine, List<ExpressionNode> expressions) {
    super(line, charPositionInLine);
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* If there are no expressions, then ARRAY literal is empty */
    if (expressions.isEmpty()) {
      return;
    }

    /* Check if the first expression's type can be resolved */
    ExpressionNode fstExpr = expressions.get(0);
    DataTypeId fstType = fstExpr.getType(symbolTable);

    if (fstType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Could not resolve type of array assignment.");
      return;
    }

    /* Check if the other elements' types can be resolved and match the first element's type */
    for (int i = 1; i < expressions.size(); i++) {
      ExpressionNode currExpr = expressions.get(i);
      DataTypeId currType = currExpr.getType(symbolTable);

      if (currType == null) {
        errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                              + " Could not resolve element type(s) in array literal."
                              + " Expected: " + fstType);
        break;
      }

      if (!(fstType.equals(currType))) {
        errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                              + " Incompatible element type(s) in array literal."
                              + " Expected: " + fstType + " Actual: " + currType);
        break;
      }

      /* Recursively call semanticAnalysis on each expression node */
      currExpr.semanticAnalysis(symbolTable, errorMessages);
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    // get the size of the array elements type
    DataTypeId type = ((ArrayType) this.getType(currSymTable)).getElemType();
    int arrElemSize = type.getSize();

    // load array size in R0
    int arrSize = expressions.size() * arrElemSize + INT_BYTES_SIZE;
    Register regR0 = Register.R0;
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, regR0, arrSize));

    // BL malloc
    internalState.addInstruction(new BranchInstruction(ConditionCode.L,
        BranchOperation.B, "malloc"));

    Register allocReg = internalState.popFreeRegister();
    internalState.addInstruction(new MovInstruction(allocReg, regR0));

    // if the array elem size is 1 byte, use STRB, otherwise use STR
    StrType strInstr = (arrElemSize == 1) ? StrType.STRB : StrType.STR;

    //iterate over the expressions, generate assembly for them and store them
    Register nextAvailable = internalState.peekFreeRegister();
    int i = INT_BYTES_SIZE;
    for (ExpressionNode expression : expressions) {
      expression.generateAssembly(internalState);
      internalState.addInstruction(new StrInstruction(strInstr, nextAvailable, allocReg, i));
      i += arrElemSize;
    }

    //TODO decide between peek and pop
    Register noOfArrElemsReg = internalState.peekFreeRegister();
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, noOfArrElemsReg, expressions.size()));
    internalState.addInstruction(new StrInstruction(StrType.STR, allocReg, noOfArrElemsReg));

    //push back and free allocReg
    internalState.pushFreeRegister(allocReg);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    if (expressions.size() == 0) {
      return new ArrayType(null);
    } else {
      return new ArrayType(expressions.get(0).getType(symbolTable));
    }
  }

  /* Returns an ArrayLiter in the form: [expr1, expr2, ..., exprN] */
  @Override
  public String toString() {
    return expressions.toString();
  }
}