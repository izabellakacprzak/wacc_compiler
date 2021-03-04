package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.Enums.*;
import InternalRepresentation.Instructions.*;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class NewPairNode extends AssignRHSNode {

  /* fstExpr: ExpressionNode corresponding to the first element of this node's PAIR
   * sndExpr: ExpressionNode corresponding to the second element of this node's PAIR */
  private final ExpressionNode fstExpr;
  private final ExpressionNode sndExpr;
  private final int NO_OF_ELEMS = 2;
  private SymbolTable currSymTable = null;


  public NewPairNode(int line, int charPositionInLine, ExpressionNode fstExpr,
                     ExpressionNode sndExpr) {
    super(line, charPositionInLine);
    this.fstExpr = fstExpr;
    this.sndExpr = sndExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on expression nodes */
    fstExpr.semanticAnalysis(symbolTable, errorMessages);
    sndExpr.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {

    internalState
        .addInstruction(
            new LdrInstruction(LdrType.LDR, Register.DEST_REG, NO_OF_ELEMS * ADDRESS_BYTES_SIZE));
    //BL malloc
    internalState.addInstruction(new BranchInstruction(
        ConditionCode.L, BranchOperation.B, "malloc"));

    Register reg = internalState.popFreeRegister();

    internalState.addInstruction(new MovInstruction(reg, Register.DEST_REG));

    //TODO move duplicate code to a new func after moving all generating assembly funcs to a new class

    /* begin fstExpr code generation */
    fstExpr.generateAssembly(internalState);

    // load fstExpr type size into R0
    int fstSize = fstExpr.getType(currSymTable).getSize();
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, Register.DEST_REG, fstSize));

    // BL malloc
    internalState.addInstruction(new BranchInstruction(
        ConditionCode.L, BranchOperation.B, "malloc"));

    StrType strInstr1 = (fstSize == 1) ? StrType.STRB : StrType.STR;
    internalState
        .addInstruction(
            new StrInstruction(strInstr1, internalState.peekFreeRegister(), Register.DEST_REG));

    internalState.addInstruction(
        new StrInstruction(StrType.STR, Register.DEST_REG, internalState.peekFreeRegister(), 0));
    /* end fstExpr code generation */

    /* begin sndExpr code generation */
    sndExpr.generateAssembly(internalState);

    // load fstExpr type size into R0
    int sndSize = sndExpr.getType(currSymTable).getSize();
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, Register.DEST_REG, sndSize));

    // BL malloc
    internalState.addInstruction(new BranchInstruction(
        ConditionCode.L, BranchOperation.B, "malloc"));

    StrType strInstr2 = (fstSize == 1) ? StrType.STRB : StrType.STR;
    internalState
        .addInstruction(
            new StrInstruction(strInstr2, internalState.peekFreeRegister(), Register.DEST_REG));

    internalState.addInstruction(
        new StrInstruction(StrType.STR, Register.DEST_REG, internalState.peekFreeRegister(),
            ADDRESS_BYTES_SIZE));
    /* end sndExpr code generation */

    internalState.pushFreeRegister(reg);
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    if (fstExpr.getType(symbolTable) == null || sndExpr.getType(symbolTable) == null) {
      return null;
    }

    return new PairType(fstExpr.getType(symbolTable), sndExpr.getType(symbolTable));
  }

  /* Returns a NewPair in the form: newpair(fst, snd) */
  @Override
  public String toString() {
    return "newpair(" + fstExpr + ", " + sndExpr + ")";
  }
}