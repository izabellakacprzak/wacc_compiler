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

  private final static int NO_OF_ELEMS = 2;
  private static final int FST = 0;
  private static final int SND = 1;

  /* fstExpr: ExpressionNode corresponding to the first element of this node's PAIR
   * sndExpr: ExpressionNode corresponding to the second element of this node's PAIR */
  private final ExpressionNode fstExpr;
  private final ExpressionNode sndExpr;
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
    internalState.addInstruction(
        new LdrInstruction(LdrType.LDR, Register.DEST_REG, NO_OF_ELEMS * ADDRESS_BYTES_SIZE));
    //BL malloc
    internalState.addInstruction(
        new BranchInstruction(ConditionCode.L, BranchOperation.B, "malloc"));

    Register pairReg = internalState.popFreeRegister();

    internalState.addInstruction(new MovInstruction(pairReg, Register.DEST_REG));

    generateElem(fstExpr, internalState, pairReg, FST);
    generateElem(sndExpr, internalState, pairReg, SND);

    internalState.pushFreeRegister(pairReg);
  }

  private void generateElem(ExpressionNode expr, InternalState internalState,
      Register pairReg, int offset) {
    /* begin expr code generation */
    expr.generateAssembly(internalState);
    Register exprReg = internalState.popFreeRegister();

    // load expr type size into R0
    int size = expr.getType(currSymTable).getSize();
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, Register.DEST_REG, size));

    // BL malloc
    internalState.addInstruction(new BranchInstruction(
        ConditionCode.L, BranchOperation.B, "malloc"));

    StrType strInstr = (size == 1) ? StrType.STRB : StrType.STR;
    internalState.addInstruction(new StrInstruction(strInstr, exprReg, Register.DEST_REG));

    internalState.addInstruction(
        new StrInstruction(StrType.STR, Register.DEST_REG, pairReg, offset * ADDRESS_BYTES_SIZE));

    internalState.pushFreeRegister(exprReg);
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