package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

import static SemanticAnalysis.DataTypes.BaseType.Type.BOOL;

public class IfStatementNode extends StatementNode {

  /* condition:     ExpressionNode representing the condition of the if statement
   * thenStatement: StatementNode representing the 'then' body of the if statement
   * elseStatement: optional StatementNode representing the 'else' body of the if statement */
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
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    DataTypeId conditionType = condition.getType(symbolTable);

    boolean isUnsetParam = condition.isUnsetParamId(symbolTable);
    ParameterId param = condition.getParamId(symbolTable);

    boolean isUnsetArrayParam = false;
    ParameterId arrayParam = null;
    ArrayElemNode arrayElem = null;

    if (condition instanceof ArrayElemNode) {
      arrayElem = (ArrayElemNode) condition;
      isUnsetArrayParam = arrayElem.isUnsetParameterIdArrayElem(symbolTable);
      arrayParam = arrayElem.getUnsetParameterIdArrayElem(symbolTable);
    }

    if (isUnsetParam) {
      param.setType(new BaseType(BOOL));
    } else if (isUnsetArrayParam) {
      arrayParam.setBaseElemType(new BaseType(BOOL));
    }

    /* Recursively call semanticAnalysis on condition node */
    condition.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that the type of the condition expression is of type BOOL */
    conditionType = condition.getType(symbolTable);

    if (conditionType == null) {
      errorMessages.add(new SemanticError(condition.getLine(), condition.getCharPositionInLine(),
          "Could not resolve type for '" + condition + "'."
              + " Expected: BOOL"));

    } else if (!conditionType.equals(new BaseType(BOOL))) {
      errorMessages.add(new SemanticError(condition.getLine(), condition.getCharPositionInLine(),
          " Incompatible type for 'If' condition."
              + " Expected: BOOL Actual: " + conditionType));
    }

    /* Recursively call semanticAnalysis on statement nodes */
    thenStatement
        .semanticAnalysis(new SymbolTable(symbolTable), errorMessages, uncheckedNodes, firstCheck);

    /* If there is an else statement call semanticAnalysis on it */
    if (elseStatement != null) {
      elseStatement.semanticAnalysis(
          new SymbolTable(symbolTable), errorMessages, uncheckedNodes, firstCheck);
    }
  }

  /* Recursively call on statement nodes */
  @Override
  public void setReturnType(DataTypeId returnType) {
    thenStatement.setReturnType(returnType);

    if (elseStatement != null) {
      elseStatement.setReturnType(returnType);
    }
  }

  /* true if both the then and else statements have either an exit or return statement.
   * Used for syntax error checking. */
  @Override
  public boolean hasReturnStatement() {
    if (elseStatement == null) {
      return thenStatement.hasReturnStatement() || thenStatement.hasExitStatement();
    }

    return (thenStatement.hasReturnStatement() || thenStatement.hasExitStatement())
        && (elseStatement.hasExitStatement() || elseStatement.hasReturnStatement());
  }

  @Override
  public boolean hasExitStatement() {
    return this.hasReturnStatement();
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor()
        .visitIfStatementNode(internalState, condition, thenStatement, elseStatement);
  }
}