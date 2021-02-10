package AbstractSyntaxTree.statement;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class NewScopeStatementNode extends StatementNode {

  private final StatementNode statement;

  public NewScopeStatementNode(StatementNode statement) {
    this.statement = statement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    statement.semanticAnalysis(new SymbolTable(symbolTable), errorMessages);

  }

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