package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class WhileStatementNode extends StatementNode {

  /* condition: ExpressionNode representing the condition of the while loop
   * statement: StatementNode representing the body of the while loop */
  private final ExpressionNode condition;
  private final StatementNode statement;

  public WhileStatementNode(ExpressionNode condition, StatementNode statement) {
    this.condition = condition;
    this.statement = statement;
  }

  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on condition node */
    condition.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the type of the condition expression is of type BOOL */
    DataTypeId conditionType = condition.getType(symbolTable);

    if (conditionType == null) {
      errorMessages.add(condition.getLine() + ":" + condition.getCharPositionInLine()
          + " Could not resolve type of '" + condition + "'."
          + " Expected: BOOL");

    } else if (!conditionType.equals(new BaseType(BaseType.Type.BOOL))) {
      errorMessages.add(condition.getLine() + ":" + condition.getCharPositionInLine()
          + " Incompatible type for 'If' condition."
          + " Expected: BOOL Actual: " + conditionType);
    }

    /* Recursively call semanticAnalysis on statement node */
    statement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
  }

  /* Recursively traverses the AST and sets the function expected return type in the ReturnNode
   * that it reaches. */
  @Override
  public void setReturnType(DataTypeId returnType) {
    statement.setReturnType(returnType);
  }

  /* Checks that the statement has a child return statement by recursively traversing the AST.
   * Used for syntax error checking. */
  @Override
  public boolean hasReturnStatement() {
    return statement.hasReturnStatement();
  }


  /* Checks that the statement has a child exit statement by recursively traversing the AST.
   * Used for syntax error checking. */
  @Override
  public boolean hasExitStatement() {
    return statement.hasExitStatement();
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitWhileStatementNode(internalState, condition, statement);
  }
}