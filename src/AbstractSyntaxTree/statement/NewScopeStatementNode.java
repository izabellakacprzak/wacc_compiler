package AbstractSyntaxTree.statement;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class NewScopeStatementNode extends StatementNode {

  /* statement:  StatementNode corresponding to the root statement contained within the new scope */
  private final StatementNode statement;
  private SymbolTable currSymTable = null;

  public NewScopeStatementNode(StatementNode statement) {
    this.statement = statement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = new SymbolTable(symbolTable);
    /* Recursively call semanticAnalysis on statement node */
    statement.semanticAnalysis(currSymTable, errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitNewScopeStatementNode(internalState, statement);
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