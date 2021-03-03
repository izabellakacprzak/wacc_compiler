package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.Enums.ConditionCode;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Instructions.CompareInstruction;
import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.InternalState;
import InternalRepresentation.Operand;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class WhileStatementNode extends StatementNode {

  /* condition: ExpressionNode representing the condition of the while loop
   * statement: StatementNode representing the body of the while loop */
  private final ExpressionNode condition;
  private final StatementNode statement;
  private SymbolTable currSymTable = null;

  public WhileStatementNode(ExpressionNode condition, StatementNode statement) {
    this.condition = condition;
    this.statement = statement;
  }

  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
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

  @Override
  public void generateAssembly(InternalState internalState) {
    String condLabel = internalState.generateNewLabel();
    String statementLabel = internalState.generateNewLabel();

    internalState.addInstruction(new BranchInstruction(BranchOperation.B, condLabel));

    internalState.addInstruction(new LabelInstruction(statementLabel));
    internalState.allocateStackSpace(statement.getCurrSymTable());
    statement.generateAssembly(internalState);
    internalState.deallocateStackSpace(statement.getCurrSymTable());
    internalState.addInstruction(new LabelInstruction(condLabel));
    condition.generateAssembly(internalState);

    internalState
        .addInstruction(new CompareInstruction(internalState.peekFreeRegister(), new Operand(1)));
    internalState.addInstruction(new BranchInstruction(
        ConditionCode.EQ, BranchOperation.B, statementLabel));
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
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}