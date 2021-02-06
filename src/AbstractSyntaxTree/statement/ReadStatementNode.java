package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReadStatementNode implements StatementNode {

  private final AssignLHSNode lhs;

  public ReadStatementNode(AssignLHSNode lhs) {
    this.lhs = lhs;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

      lhs.semanticAnalysis(symbolTable, errorMessages);
      // check type of lhs - read allows only INT or CHAR
  }
}