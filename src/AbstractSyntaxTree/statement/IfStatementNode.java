package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class IfStatementNode extends StatementNode {

  /* condition:     ExpressionNode representing the condition of the if statement
   * thenStatement: StatementNode representing the 'then' body of the if statement
   * elseStatement: StatementNode representing the 'else' body of the if statement */
  private final ExpressionNode condition;
  private final StatementNode thenStatement;
  private final StatementNode elseStatement;

  public IfStatementNode(ExpressionNode condition, StatementNode thenStatement,
      StatementNode elseStatement) {
    this.condition = condition;
    this.thenStatement = thenStatement;
    this.elseStatement = elseStatement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on condition node */
    condition.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the type of the condition expression is of type BOOL */
    DataTypeId conditionType = condition.getType(symbolTable);

    if (conditionType == null) {
      errorMessages.add(condition.getLine() + ":" + condition.getCharPositionInLine()
          + " Could not resolve type for '" + condition + "'."
          + " Expected: BOOL");

    } else if (!conditionType.equals(new BaseType(BaseType.Type.BOOL))) {
      errorMessages.add(condition.getLine() + ":" + condition.getCharPositionInLine()
          + " Incompatible type for 'If' condition."
          + " Expected: BOOL Actual: " + conditionType);
    }

    /* Recursively call semanticAnalysis on statement nodes */
    thenStatement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
    elseStatement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
            visitIfStatementNode(internalState, condition, thenStatement, elseStatement);
  }

  /* Recursively call on statement nodes */
  @Override
  public void setReturnType(DataTypeId returnType) {
    thenStatement.setReturnType(returnType);
    elseStatement.setReturnType(returnType);
  }

  /* true if both the then and else statements have either an exit or return statement.
   * Used for syntax error checking. */
  @Override
  public boolean hasReturnStatement() {
    return (thenStatement.hasReturnStatement() || thenStatement.hasExitStatement())
        && (elseStatement.hasExitStatement() || elseStatement.hasReturnStatement());
  }

  @Override
  public boolean hasExitStatement() {
    return this.hasReturnStatement();
  }

}