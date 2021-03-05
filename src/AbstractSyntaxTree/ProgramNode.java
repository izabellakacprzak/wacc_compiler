package AbstractSyntaxTree;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode implements ASTNode {

  /* statementNode: Root node of program's statements
   * functionNode:  List of all function declaration nodes
   * syntaxErrors:  List to add any syntax error messages for syntax errors */
  private final StatementNode statementNode;
  private final List<FunctionNode> functionNodes;
  private final List<String> syntaxErrors;
  private SymbolTable currSymTable;


  public ProgramNode(StatementNode statementNode, List<FunctionNode> functionNodes) {
    this.statementNode = statementNode;
    this.functionNodes = functionNodes;
    syntaxErrors = new ArrayList<>();
  }

  /* Check to see whether any syntax errors are found in each function nodes of the  program.
   * Accumulates them and returns them to notify the syntax error listener. */
  public List<String> checkSyntaxErrors() {
    String error;
    for (FunctionNode f : functionNodes) {
      error = f.checkSyntaxErrors();
      if (!error.isEmpty()) {
        syntaxErrors.add(error);
      }
    }
    return syntaxErrors;
  }

  @Override
  public void semanticAnalysis(SymbolTable topSymbolTable, List<String> errorMessages) {
    setCurrSymTable(topSymbolTable);

    for (FunctionNode func : functionNodes) {
      /* Create a new SymbolTable for the function's scope */
      func.setCurrSymTable(new SymbolTable(topSymbolTable));

      if (topSymbolTable.lookupAll(func.getName()) != null) {
        /* A function with the same name has already been declared */
        IdentifierNode id = func.getIdentifierNode();
        errorMessages.add(id.getLine() + ":" + id.getCharPositionInLine()
            + " Function '" + func + "' has already been declared.");

      } else {
        /* Create function identifier and add it to the topSymbolTable */
        FunctionId identifier = (FunctionId) func.getIdentifier(func.getCurrSymTable());
        topSymbolTable.add(func.getName(), identifier);
      }
    }

    /* Call semanticAnalysis on each function, even if it has been declared twice */
    for (FunctionNode func : functionNodes) {
      func.semanticAnalysis(func.getCurrSymTable(), errorMessages);
    }

    /* Call semanticAnalysis on the root statement node to analysis the rest of the program */
    statementNode.semanticAnalysis(topSymbolTable, errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
        visitProgramNode(internalState, statementNode, functionNodes);
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}