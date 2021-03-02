package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Enums.*;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.Instructions.StrInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class PairElemNode extends AssignRHSNode {

  /* Used to check if position is for 'fst' or 'snd' */
  private static final int FST = 0;

  /* position:   0 if 'fst' was called, otherwise 'snd' was called.
   * expression: ExpressionNode called with 'fst' or 'snd'. Should be an IdentifierNode */
  private final int position;
  private final ExpressionNode expression;
  private SymbolTable currSymTable = null;

  public PairElemNode(int line, int charPositionInLine, int position, ExpressionNode expression) {
    super(line, charPositionInLine);
    this.position = position;
    this.expression = expression;
  }

  public int getPosition() {
    return position;
  }

  public String getIdentifier() {

    if (!(expression instanceof IdentifierNode)) {
      return null;
    }
    return ((IdentifierNode) expression).getIdentifier();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages);

    /* Check that expression is an IdentifierNode */
    if (!(expression instanceof IdentifierNode)) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
                            + " Invalid identifier. Expected: PAIR IDENTIFIER Actual: '" + expression + "'");
      return;
    }

    /* Check that the the identifier (pairId)'s type can be resolved and is of the PAIR type */
    IdentifierNode pairId = (IdentifierNode) expression;
    DataTypeId expectedType = pairId.getType(symbolTable);

    if (expectedType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Could not resolve type of '" + pairId + "'. Expected: PAIR");

    } else if (!(expectedType instanceof PairType)) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
                            + " Incompatible type of '" + expression + "'. "
                            + " Expected: PAIR Actual: " + expectedType);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    expression.generateAssembly(internalState);
    Register reg = internalState.peekFreeRegister();

    internalState.addInstruction(new MovInstruction(Register.R0, reg));

    BuiltInFunction.NULL_POINTER.setUsed();
    internalState.addInstruction(new BranchInstruction(BranchOperation.BL, BuiltInFunction.NULL_POINTER.getLabel()));
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, reg, reg, position * ADDRESS_BYTES_SIZE));

    PairType pair = (PairType) expression.getType(currSymTable);
    DataTypeId type = (position == FST) ? pair.getFstType() : pair.getSndType();
    int elemSize = type.getSize();
    StrType strInstr = (elemSize == 1) ? StrType.STRB : StrType.STR;

    internalState.addInstruction(new StrInstruction(strInstr, reg, reg));
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    /* Check that expression is an IdentifierNode */
    if (!(expression instanceof IdentifierNode)) {
      return null;
    }

    /* Check that the the identifier (pairId)'s type can be resolved and is of the PAIR type */
    IdentifierNode pairId = (IdentifierNode) expression;
    DataTypeId pairType = pairId.getType(symbolTable);

    if (!(pairType instanceof PairType)) {
      return null;
    }

    /* Return the type corresponding to the position and IdentifierNode pairId */
    return position == FST ? ((PairType) pairType).getFstType()
               : ((PairType) pairType).getSndType();
  }

  /* Returns a PairElem in the form: (fst | snd) expr */
  @Override
  public String toString() {
    if (position == FST) {
      return "fst " + expression;
    } else {
      return "snd " + expression;
    }
  }
}

