package AbstractSyntaxTree.expression;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ParenthesisExprNode extends ExpressionNode {

  /* innerExpr:    ExpressionNode corresponding to the expression within parenthesis */
  private final ExpressionNode innerExpr;

  public ParenthesisExprNode(int line, int charPositionInLine, ExpressionNode innerExpr) {
    super(line, charPositionInLine);
    this.innerExpr = innerExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on expression node */
    innerExpr.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitParenthesisExprNode(internalState, innerExpr);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return innerExpr.getType(symbolTable);
  }

  @Override
  public String toString() {
    return "(" + innerExpr + ")";
  }
}