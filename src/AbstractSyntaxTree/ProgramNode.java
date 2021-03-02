package AbstractSyntaxTree;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import InternalRepresentation.Enums.Directive;
import InternalRepresentation.Enums.LdrType;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.*;
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
    // add main label and push Link Register
    internalState.addInstruction(new LabelInstruction("main"));
    internalState.addInstruction(new PushInstruction(Register.LR));

    // generate assembly code for all function nodes
    for (FunctionNode functionNode : functionNodes) {
      functionNode.generateAssembly(internalState);
    }

    //TODO allocate stack space for variables. How to get the var symbol table??
    // TODO size should include function calls params sizes ???????
    internalState.allocateStackSpace(statementNode.getCurrSymTable());

    statementNode.generateAssembly(internalState);

    internalState.deallocateStackSpace(statementNode.getCurrSymTable());

    internalState.addInstruction(new LdrInstruction(LdrType.LDR, Register.R0, 0));
    internalState.addInstruction(new PopInstruction(Register.PC));
    internalState.addInstruction(new DirectiveInstruction(Directive.LTORG));
  }
}