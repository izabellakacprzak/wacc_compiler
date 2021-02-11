package AbstractSyntaxTree.type;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FunctionNode implements TypeNode {

  private final TypeNode returnType;
  private final IdentifierNode identifier;
  private final ParamListNode params;
  private final StatementNode bodyStatement;

  private SymbolTable currSymTable = null;

  public FunctionNode(TypeNode returnType, IdentifierNode identifier,
      ParamListNode params, StatementNode bodyStatement) {
    this.returnType = returnType;
    this.identifier = identifier;
    this.params = params;
    this.bodyStatement = bodyStatement;
  }

  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

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
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    bodyStatement.setReturnType(returnType.getType());

    params.semanticAnalysis(symbolTable, errorMessages);
    bodyStatement.semanticAnalysis(symbolTable, errorMessages);
    returnType.semanticAnalysis(symbolTable, errorMessages);
  }

  public String getName() {
    // Add a '*' character in front of function name to cover the case of having
    // functions and variables of the same name in symTable
    return "*" + identifier.getIdentifier();
  }

  public IdentifierNode getIdentifierNode() {
    return identifier;
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {

    return new FunctionId(identifier, returnType.getType(),
        params.getIdentifiers(symbolTable));
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