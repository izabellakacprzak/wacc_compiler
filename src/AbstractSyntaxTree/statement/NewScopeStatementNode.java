package AbstractSyntaxTree.statement;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class NewScopeStatementNode extends StatementNode {

  /* statement:  StatementNode corresponding to the root statement contained within the new scope */
  private final StatementNode statement;

  public NewScopeStatementNode(StatementNode statement) {
    this.statement = statement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on statement node */
    statement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);
  }

  // TODO: idk bout this comment either
  /* Recursively call override methods on statement node */
  @Override
  public boolean hasReturnStatement() {
    return statement.hasReturnStatement();
  }

  @Override
  public boolean hasExitStatement() {
    return statement.hasExitStatement();
  }

  @Override
  public void setReturnType(DataTypeId returnType) {
    statement.setReturnType(returnType);
  }
}