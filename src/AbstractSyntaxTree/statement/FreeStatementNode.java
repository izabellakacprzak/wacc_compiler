package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;


public class FreeStatementNode extends StatementNode {

  /* expression:  ExpressionNode corresponding to the expression 'free' was called with */
  private final ExpressionNode expression;
  private SymbolTable currSymTable = null;

  public FreeStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the type of assignment is an ARRAY or a PAIR */
    DataTypeId exprType = expression.getType(symbolTable);

    if (exprType == null) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
          + " Could not resolve type for '" + expression + "'."
          + " Expected: ARRAY, PAIR");

    } else if (!exprType.equals(new PairType(null, null)) &&
        !exprType.equals(new ArrayType(null))) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
          + " Incompatible type for 'free' statement." +
          " Expected: ARRAY, PAIR Actual: " + exprType);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitFreeStatementNode(internalState, expression);
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}
