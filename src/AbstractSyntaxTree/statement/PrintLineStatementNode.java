package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PrintLineStatementNode extends StatementNode {

  /* expression:  ExpressionNode corresponding to the expression 'println' was called with */
  private final ExpressionNode expression;
  private SymbolTable currSymTable = null;

  public PrintLineStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages);
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}