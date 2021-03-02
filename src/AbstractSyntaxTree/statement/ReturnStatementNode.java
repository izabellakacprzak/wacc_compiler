package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.Instructions.PopInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReturnStatementNode extends StatementNode {

  /* returnExpr:  ExpressionNode corresponding to the expression 'return' was called with
   * returnType:  DataTypeId of the expected return type according to the function's declaration */
  private final ExpressionNode returnExpr;
  private DataTypeId returnType;
  private SymbolTable currSymTable = null;

  public ReturnStatementNode(ExpressionNode returnExpr) {
    this.returnExpr = returnExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on expression node */
    returnExpr.semanticAnalysis(symbolTable, errorMessages);

    /* Check to see if the current symbolTable is the top table.
     * Return statements cannot be present in the body of the main function */
    if (symbolTable.isTopSymTable()) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
          + " 'return' statement cannot be present in the body of the main function.");
      return;
    }

    /* Check that the type of returnExpr is the same as the expected returnType */
    DataTypeId returnExprType = returnExpr.getType(symbolTable);

    if (returnExprType == null) {
      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
          + " Could not resolve type for '" + returnExpr + "'.");
    } else if (!(returnExprType.equals(returnType)) && !stringToCharArray(returnType,
        returnExprType)) {

      errorMessages.add(returnExpr.getLine() + ":" + returnExpr.getCharPositionInLine()
          + " Declared return type does not match 'return' statement type."
          + " Expected: " + returnType + " Actual: " + returnExprType);
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    Register returnStatReg = internalState.peekFreeRegister();
    returnExpr.generateAssembly(internalState);

    internalState.addInstruction(new MovInstruction(Register.R0, returnStatReg));
    internalState.addInstruction(new PopInstruction(Register.PC));
  }

  /* Sets the function expected returnType  */
  @Override
  public void setReturnType(DataTypeId returnType) {
    this.returnType = returnType;
  }

  /* true for this StatementNode as it represents a return statement. Used for syntax error
   * checking. */
  @Override
  public boolean hasReturnStatement() {
    return true;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}
