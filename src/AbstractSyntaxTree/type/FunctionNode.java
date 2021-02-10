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
    boolean error = !bodyStatement.hasReturnStatement() && !bodyStatement.hasExitStatement();
    if (error) {
      errorMessage
          .append("Function " + identifier.getIdentifier() + " has no return or exit statement\n");
    } else {
      error = bodyStatement.hasReturnStatement() && !bodyStatement.hasNoStatementAfterReturn();
      if (error) {
        errorMessage.append("Function " + identifier.toString() + " has statements after return\n");
      }
    }

    return errorMessage.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    bodyStatement.setReturnType(returnType.getType());

    returnType.semanticAnalysis(symbolTable, errorMessages);
    //identifier.semanticAnalysis(symbolTable, errorMessages);
    params.semanticAnalysis(symbolTable, errorMessages);
    bodyStatement.semanticAnalysis(symbolTable, errorMessages);
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
  public Identifier getIdentifier(SymbolTable parentSymTable) {
    return new FunctionId(this, (DataTypeId) returnType.getIdentifier(parentSymTable),
        params.getIdentifiers(parentSymTable), currSymTable);
  }

  @Override
  public DataTypeId getType() {
    return returnType.getType();
  }
}