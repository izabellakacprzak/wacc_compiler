package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class PairElemNode extends AssignRHSNode {

  /* Used to check if position is for 'fst' or 'snd' */
  private static final int FST = 0;

  /* position:   0 if 'fst' was called, otherwise 'snd' was called.
   * expression: ExpressionNode called with 'fst' or 'snd'. Should be an IdentifierNode */
  private final int position;
  private final ExpressionNode expression;

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
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that expression is an IdentifierNode */
    if (!(expression instanceof IdentifierNode)) {
      errorMessages.add(new SemanticError(expression.getLine(), expression.getCharPositionInLine(),
          "Invalid identifier. Expected: PAIR IDENTIFIER Actual: '" + expression + "'"));
      return;
    }

    /* Check that the the identifier (pairId)'s type can be resolved and is of the PAIR type */
    IdentifierNode pairId = (IdentifierNode) expression;
    DataTypeId expectedType = pairId.getType(symbolTable);

    if (expectedType == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Could not resolve type of '" + pairId + "'. Expected: PAIR"));

    } else if (!(expectedType instanceof PairType)) {
      errorMessages.add(new SemanticError(expression.getLine(), expression.getCharPositionInLine(),
          "Incompatible type of '" + expression + "'. "
              + " Expected: PAIR Actual: " + expectedType));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
        visitPairElemNode(internalState, expression, position, getCurrSymTable());
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

