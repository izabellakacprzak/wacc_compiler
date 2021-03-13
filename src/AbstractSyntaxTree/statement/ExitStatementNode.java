package AbstractSyntaxTree.statement;

import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ExitStatementNode extends StatementNode {

  /* expression:   ExpressionNode corresponding to the expression 'exit' was called with */
  private final ExpressionNode expression;

  public ExitStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    if (expression.isUnsetParamId(symbolTable)) {
      ParameterId exprParam = expression.getParamId(symbolTable);
      exprParam.setType(new BaseType(INT));
    }

    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that the type of assignment is an INT */
    DataTypeId exprType = expression.getType(symbolTable);

    if (exprType == null) {
      errorMessages.add(new SemanticError(expression.getLine(), expression.getCharPositionInLine(),
          "Could not resolve type for '" + expression + "'."
              + " Expected: INT"));
    } else if (!exprType.equals(new BaseType(INT))) {
      errorMessages.add(new SemanticError(expression.getLine(), expression.getCharPositionInLine(),
          "Incompatible type for 'exit' statement."
              + " Expected: INT Actual: " + exprType));
    }
  }

  /* Flags that ExitStatementNode represents an exit statement. Used for syntax errors checking
   * when traversing the AST. */
  @Override
  public boolean hasExitStatement() {
    return true;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitExitStatementNode(internalState, expression);
  }
}