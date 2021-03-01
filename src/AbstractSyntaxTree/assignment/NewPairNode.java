package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.ConditionCode;
import InternalRepresentation.Enums.Condition;
import InternalRepresentation.Enums.LdrType;
import InternalRepresentation.Enums.Reg;
import InternalRepresentation.Enums.StrType;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.Instructions.StrInstruction;
import InternalRepresentation.InternalState;
import InternalRepresentation.Register;
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
    /* Recursively call semanticAnalysis on expression nodes */
    fstExpr.semanticAnalysis(symbolTable, errorMessages);
    sndExpr.semanticAnalysis(symbolTable, errorMessages);
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    //TODO don't create new obj for R0
    Register r0Reg = new Register(Reg.R0);

    internalState.addInstruction(new LdrInstruction(LdrType.LDR, r0Reg, NO_OF_ELEMS * ADDRESS_BYTES_SIZE));
    //BL malloc
    internalState.addInstruction(new BranchInstruction(new ConditionCode(Condition.L), "malloc"));

    Register reg = internalState.popFreeRegister();

    internalState.addInstruction(new MovInstruction(reg, r0Reg));

    /* begin fstExpr code generation */
    fstExpr.generateAssembly(internalState);


    // load fstExpr type size into R0
    int fstSize = fstExpr.getType(currSymTable).getSize();
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, r0Reg, fstSize));

    // BL malloc
    internalState.addInstruction(new BranchInstruction(new ConditionCode(Condition.L), "malloc"));

    StrType strInstr1 = (fstSize == 1) ? StrType.STRB : StrType.STR;
    internalState.addInstruction(new StrInstruction(strInstr1, internalState.peekFreeRegister(), r0Reg));

    internalState.addInstruction(new StrInstruction(StrType.STR, r0Reg, internalState.peekFreeRegister(), 0));
    /* end fstExpr code generation */

    /* begin sndExpr code generation */
    sndExpr.generateAssembly(internalState);

    // load fstExpr type size into R0
    int sndSize = sndExpr.getType(currSymTable).getSize();
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, r0Reg, sndSize));

    // BL malloc
    internalState.addInstruction(new BranchInstruction(new ConditionCode(Condition.L), "malloc"));

    StrType strInstr2 = (fstSize == 1) ? StrType.STRB : StrType.STR;
    internalState.addInstruction(new StrInstruction(strInstr2, internalState.peekFreeRegister(), r0Reg));

    internalState.addInstruction(new StrInstruction(StrType.STR, r0Reg, internalState.peekFreeRegister(), ADDRESS_BYTES_SIZE));
    /* end sndExpr code generation */

    internalState.pushFreeRegister(reg);
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