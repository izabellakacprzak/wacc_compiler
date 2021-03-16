package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class FunctionNode implements TypeNode {

  /* returnType:    TypeNode of the return type
   * identifier:    IdentifierNode of the function's declaration
   * params:        ParamListNode containing a list of the function's parameter
   *                  IdentifierNodes and TypeNodes
   * bodyStatement: Root node for the statement tree of the function's body
   * currSymTable:  set to the node's SymbolTable for the current scope during semanticAnalysis */
  private final TypeNode returnType;
  private final IdentifierNode identifier;
  private final ParamListNode params;
  private final StatementNode bodyStatement;

  /* currSymTable: stores the SymbolTable corresponding to the function's scope.
   * Set before semanticAnalysis begins in ProgramNode */
  private SymbolTable currSymTable = null;

  public FunctionNode(TypeNode returnType, IdentifierNode identifier,
      ParamListNode params, StatementNode bodyStatement) {
    this.returnType = returnType;
    this.identifier = identifier;
    this.params = params;
    this.bodyStatement = bodyStatement;
  }

  /* Returns the function identifier string as is stored in the symbol table.
   * Character '*' added to differentiate from variable and parameter names */
  public String getName() {
    return "*" + identifier.getIdentifier();
  }

  /* Returns identifier without extra '*' character */
  public IdentifierNode getIdentifierNode() {
    return identifier;
  }

  /* Check for whether the function has either an exit statement or return statement
   * or if there are statements after a return statement in the function body */
  public String checkSyntaxErrors() {
    StringBuilder errorMessage = new StringBuilder();
    if ((!bodyStatement.hasReturnStatement() && !bodyStatement.hasExitStatement())
        || (bodyStatement.hasReturnStatement() && !bodyStatement.hasNoStatementAfterReturn())) {
      errorMessage.append("Syntax error at line ").append(identifier.getLine()).append(":")
          .append(identifier.getCharPositionInLine()).append(" function ")
          .append(identifier.getIdentifier())
          .append(" does not end with a return or exit statement");
    }

    return errorMessage.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Set the expected return type in the corresponding ReturnStatementNode of the
     * function body */
    bodyStatement.setReturnType(returnType.getType());

    /* Recursively call semanticAnalysis on each stored node */
    params.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    bodyStatement.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    returnType.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
        visitFunctionNode(internalState, identifier, params, getType(), bodyStatement, currSymTable);
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return new FunctionId(identifier, returnType.getType(), params.getIdentifiers(symbolTable));
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public DataTypeId getType() {
    return returnType.getType();
  }

  @Override
  public String toString() {
    return identifier.getIdentifier();
  }
}