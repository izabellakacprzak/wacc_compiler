package AbstractSyntaxTree;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.ClassNode;
import AbstractSyntaxTree.type.FunctionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode implements ASTNode {

  /* statementNode: Root node of program's statements
   * functionNode:  List of all function declaration nodes
   * syntaxErrors:  List to add any syntax error messages for syntax errors */
  private final StatementNode statementNode;
  private final List<FunctionNode> functionNodes;
  private final List<ClassNode> classNodes;
  private final List<String> syntaxErrors;
  private SymbolTable currSymTable;

  public ProgramNode(StatementNode statementNode, List<FunctionNode> functionNodes, List<ClassNode> classNodes) {
    this.statementNode = statementNode;
    this.functionNodes = functionNodes;
    this.classNodes = classNodes;
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

  public static void overloadFunc(SymbolTable symbolTable, List<SemanticError> errorMessages, List<FunctionNode> methods,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    for (FunctionNode method : methods) {
      /* Create a new SymbolTable for the function's scope */
      method.setCurrSymTable(new SymbolTable(symbolTable));

      Identifier declaredFunc = symbolTable.lookupAll(method.getName());
      FunctionId newIdentifier = (FunctionId) method.getIdentifier(method.getCurrSymTable());

      if ( declaredFunc != null) {
        /* A function with the same name has already been declared
         * Extension: check if function can be overloaded */
        if (declaredFunc instanceof FunctionId) {
          OverloadFuncId overloadFunc = new OverloadFuncId((FunctionId) declaredFunc);
          if (overloadFunc.addNewFunc(newIdentifier)) {
            /* Replace function ID with new Overload function ID*/
            symbolTable.remove(method.getName());
            symbolTable.add(method.getName(), overloadFunc);
          } else {
            IdentifierNode id = method.getIdentifierNode();
            errorMessages.add(new SemanticError(id.getLine(),id.getCharPositionInLine(),
                    "Cannot overload " + declaredFunc.toString() + "' as a function with the same " +
                    "signature already exists."));
          }
        } else if (declaredFunc instanceof OverloadFuncId) {
          OverloadFuncId overloadFunc = (OverloadFuncId) declaredFunc;
          if (!overloadFunc.addNewFunc(newIdentifier)) {
            IdentifierNode id = method.getIdentifierNode();
            errorMessages.add(new SemanticError(id.getLine(), id.getCharPositionInLine(),
                "Cannot overload " + declaredFunc.toString() + "' as a function with the same " +
                    "signature already exists."));
          }
        } else {
          IdentifierNode id = method.getIdentifierNode();
          errorMessages.add(new SemanticError(id.getLine(), id.getCharPositionInLine(),
                  "Type of '" + declaredFunc.toString() + "' could not be resolved."));
        }
      } else {
        symbolTable.add(method.getName(), newIdentifier);
      }
    }

    for (FunctionNode method : methods) {
      method.semanticAnalysis(method.getCurrSymTable(), errorMessages, uncheckedNodes, firstCheck);
    }
  }

  @Override
  public void semanticAnalysis(SymbolTable topSymbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(topSymbolTable);

    /* Call overloadFunc to do semantic analysis on all declared functions and
     * check if overload is possible */
    overloadFunc(topSymbolTable, errorMessages, functionNodes, uncheckedNodes, firstCheck);

    /* Semantically analyse all classes */
    for (ClassNode classDecl : classNodes) {
      classDecl.setCurrSymTable(new SymbolTable(topSymbolTable));

      if (topSymbolTable.lookupAll(classDecl.getName()) != null) {
        /* Class with the same name has been declared */
        IdentifierNode id = classDecl.getIdentifierNode();
        errorMessages.add(new SemanticError(id.getLine(), id.getCharPositionInLine(),
                "Class '" + id.getIdentifier() + "' has already been defined."));
      } else {
        ClassType classType = (ClassType) classDecl.getIdentifier(classDecl.getCurrSymTable());
        topSymbolTable.add(classDecl.getName(), classType);
        classDecl.getCurrSymTable().add(classDecl.getName(), classType);
      }
    }

    for (ClassNode classNode : classNodes) {
      classNode.semanticAnalysis(classNode.getCurrSymTable(), errorMessages, uncheckedNodes,
          firstCheck);
    }

    /* Call semanticAnalysis on the root statement node to analysis the rest of the program */
    statementNode.semanticAnalysis(topSymbolTable, errorMessages, uncheckedNodes, firstCheck);

  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitProgramNode(internalState, statementNode, functionNodes, classNodes);
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