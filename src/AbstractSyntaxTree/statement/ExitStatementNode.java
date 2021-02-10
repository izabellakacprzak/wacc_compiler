package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ExitStatementNode extends StatementNode {

  private final ExpressionNode expr;

  public ExitStatementNode(ExpressionNode expr) {
    this.expr = expr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    expr.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId exprType = expr.getType(symbolTable);

    if (exprType == null) {
      errorMessages.add(expr.getLine() + ":" + expr.getCharPositionInLine()
              + " Could not resolve exit code type" );
    } else if (!exprType.equals(new BaseType(BaseType.Type.INT))) {
      errorMessages.add(expr.getLine() + ":" + expr.getCharPositionInLine()
              + " Exit code must be of type INT not" + exprType.toString());
    }
  }

  @Override

  public boolean hasExitStatement() {
    return true;
  }
}