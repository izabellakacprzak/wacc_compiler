package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ExitStatementNode extends StatementNode {

  /* expression:  ExpressionNode corresponding to the expression 'exit' was called with */
  private final ExpressionNode expression;

  public ExitStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
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
  public boolean hasExitStatement() {
    return true;
  }
}