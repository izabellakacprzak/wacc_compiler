package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ReturnStatementNode extends StatementNode {

  /* returnExpr: ExpressionNode corresponding to the expression 'return' was called with
   * returnType: DataTypeId of the expected return type according to the function's declaration */
  private final ExpressionNode returnExpr;
  private DataTypeId returnType;

  public ReturnStatementNode(ExpressionNode returnExpr) {
    this.returnExpr = returnExpr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    if (returnExpr.isUnsetParamId(symbolTable)) {
      ParameterId param = returnExpr.getParamId(symbolTable);
      param.setType(returnType);
    }

    /* Check that the type of returnExpr is the same as the expected returnType */
    DataTypeId returnExprType = returnExpr.getType(symbolTable);

    /* Recursively call semanticAnalysis on expression node */
    returnExpr.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check to see if the current symbolTable is the top table.
     * Return statements cannot be present in the body of the main function */
    if (symbolTable.isTopSymTable()) {
      errorMessages.add(new SemanticError(returnExpr.getLine(), returnExpr.getCharPositionInLine(),
          "'return' statement cannot be present in the body of the main function."));
      return;
    }


    if (returnExprType == null) {
      errorMessages.add(new SemanticError(returnExpr.getLine(), returnExpr.getCharPositionInLine(),
          "Could not resolve type for '" + returnExpr + "'."));
    } else if (!(returnExprType.equals(returnType)) && !stringToCharArray(returnType,
        returnExprType)) {

      errorMessages.add(new SemanticError(returnExpr.getLine(), returnExpr.getCharPositionInLine(),
          "Declared return type does not match 'return' statement type."
              + " Expected: " + returnType + " Actual: " + returnExprType));
    }
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
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitReturnStatementNode(internalState, returnExpr);
  }
}
