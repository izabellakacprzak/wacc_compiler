package AbstractSyntaxTree;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.OverloadFuncId;
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

    for (FunctionNode newFunction : functionNodes) {
      /* Create a new SymbolTable for the function's scope */
      newFunction.setCurrSymTable(new SymbolTable(topSymbolTable));

      Identifier declaredFunc = topSymbolTable.lookupAll(newFunction.getName());
      FunctionId newIdentifier = (FunctionId) newFunction.getIdentifier(newFunction.getCurrSymTable());

      if ( declaredFunc != null) {
        /* A function with the same name has already been declared
         * Extension: check if function can be overloaded */
        if (declaredFunc instanceof FunctionId) {
          OverloadFuncId overloadFunc = new OverloadFuncId((FunctionId) declaredFunc);
          if (overloadFunc.addNewFunc(newIdentifier)) {
            /* Replace function ID with new Overload function ID*/
            topSymbolTable.remove(newFunction.getName());
            topSymbolTable.add(newFunction.getName(), overloadFunc);
          } else {
            IdentifierNode id = newFunction.getIdentifierNode();
            errorMessages.add(id.getLine() + ":" + id.getCharPositionInLine()
                    + " Cannot overload " + declaredFunc.toString() + "' as a function with the same " +
                    "signature already exists.");
          }
        } else if (declaredFunc instanceof OverloadFuncId) {
          OverloadFuncId overloadFunc = (OverloadFuncId) declaredFunc;
          if (!overloadFunc.addNewFunc(newIdentifier)) {
            IdentifierNode id = newFunction.getIdentifierNode();
            errorMessages.add(id.getLine() + ":" + id.getCharPositionInLine()
                    + " Cannot overload " + declaredFunc.toString() + "' as a function with the same " +
                    "signature already exists.");
          }
        } else {
          IdentifierNode id = newFunction.getIdentifierNode();
          errorMessages.add(id.getLine() + ":" + id.getCharPositionInLine()
                  + " Type of '" + declaredFunc.toString() + "' could not be resolved.");
        }
      } else {
        topSymbolTable.add(newFunction.getName(), newIdentifier);
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
    internalState.getCodeGenVisitor().visitProgramNode(internalState, statementNode, functionNodes);
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