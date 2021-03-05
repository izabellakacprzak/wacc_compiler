package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ExitStatementNode extends StatementNode {

  /* expression:  ExpressionNode corresponding to the expression 'exit' was called with */
  private final ExpressionNode expression;
  private SymbolTable currSymTable = null;

  public ExitStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the type of assignment is an INT */
    DataTypeId exprType = expression.getType(symbolTable);

    if (exprType == null) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
          + " Could not resolve type for '" + expression + "'."
          + " Expected: INT");
    } else if (!exprType.equals(new BaseType(BaseType.Type.INT))) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
          + " Incompatible type for 'exit' statement."
          + " Expected: INT Actual: " + exprType);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
            visitExitStatementNode(internalState, expression);
  }

  /* Flags that ExitStatementNode represents an exit statement. Used for syntax errors checking
   * when traversing the AST. */
  @Override
  public boolean hasExitStatement() {
    return true;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}