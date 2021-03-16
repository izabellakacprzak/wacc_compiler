package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class NewPairNode extends AssignRHSNode {

  /* fstExpr:      ExpressionNode corresponding to the first element of this node's PAIR
   * sndExpr:      ExpressionNode corresponding to the second element of this node's PAIR */
  private final ExpressionNode fstExpr;
  private final ExpressionNode sndExpr;


  public NewPairNode(int line, int charPositionInLine, ExpressionNode fstExpr,
      ExpressionNode sndExpr) {
    super(line, charPositionInLine);
    this.fstExpr = fstExpr;
    this.sndExpr = sndExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on expression nodes */
    fstExpr.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    sndExpr.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor()
        .visitNewPairNode(internalState, fstExpr, sndExpr, getCurrSymTable());
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