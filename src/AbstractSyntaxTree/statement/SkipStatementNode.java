package AbstractSyntaxTree.statement;

import InternalRepresentation.InternalState;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class SkipStatementNode extends StatementNode {

  SymbolTable currSymTable = null;

  public String toString() {
    return "skip";
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
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
