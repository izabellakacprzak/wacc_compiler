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

public class WhileStatementNode extends StatementNode {

  /* condition: ExpressionNode representing the condition of the while loop
   * statement: StatementNode representing the body of the while loop */
  private final ExpressionNode condition;
  private final StatementNode statement;

  public WhileStatementNode(ExpressionNode condition, StatementNode statement) {
    this.condition = condition;
    this.statement = statement;
  }

  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* If the condition's type needs to be inferred, set the type to BOOL */
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
          "Could not resolve type of '" + condition + "'."
              + " Expected: BOOL"));

    } else if (!conditionType.equals(new BaseType(BaseType.Type.BOOL))) {
      errorMessages.add(new SemanticError(condition.getLine(), condition.getCharPositionInLine(),
          "Incompatible type for 'While' condition."
              + " Expected: BOOL Actual: " + conditionType));
    }

    /* Recursively call semanticAnalysis on statement node */
    statement
        .semanticAnalysis(new SymbolTable(symbolTable), errorMessages, uncheckedNodes, firstCheck);
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