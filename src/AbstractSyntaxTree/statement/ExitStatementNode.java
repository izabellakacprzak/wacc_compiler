package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ExitStatementNode extends StatementNode {

  /* expression:  ExpressionNode corresponding to the expression 'exit' was called with */
  private final ExpressionNode expression;
  private SymbolTable currSymTable = null;

  public ExitStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the type of assignment is an INT */
    DataTypeId exprType = expression.getType(symbolTable);

    if (exprType == null) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
          + " Could not resolve type for '" + expression + "'."
          + " Expected: INT");
    } else if (!exprType.equals(new BaseType(BaseType.Type.INT))) {
      errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
          + " Incompatible type for 'exit' statement."
          + " Expected: INT Actual: " + exprType);
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    Register exitCodeReg = internalState.peekFreeRegister();

    expression.generateAssembly(internalState);

    internalState.addInstruction(new MovInstruction(Register.R0, exitCodeReg));
    internalState.addInstruction(new BranchInstruction(("exit"), BranchOperation.BL));
  }

  /* Flags that ExitStatementNode represents an exit statement. Used for syntax errors checking
   * when traversing the AST. */
  @Override
  public boolean hasExitStatement() {
    return true;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}