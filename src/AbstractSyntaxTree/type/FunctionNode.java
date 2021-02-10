package AbstractSyntaxTree.type;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;

public class FunctionNode extends TypeNode {
  private TypeNode returnType;
  private IdentifierNode identifier;
  private ParamListNode params;
  private StatementNode bodyStatement;

  public FunctionNode(TypeNode returnType, IdentifierNode identifier,
                      ParamListNode params, StatementNode bodyStatement) {
    this.returnType = returnType;
    this.identifier = identifier;
    this.params = params;
    this.bodyStatement = bodyStatement;
  }


  public String checkSyntaxErrors() {
    StringBuilder errorMessage = new StringBuilder();
    boolean error = !bodyStatement.hasReturnStatement() && !bodyStatement.hasExitStatement();
    if(error) {
      errorMessage.append("Function " + identifier.getIdentifier() + " has no return or exit statement\n");
    } else {
      error = bodyStatement.hasReturnStatement() && !bodyStatement.hasNoStatementAfterReturn();
      if (error) {
        errorMessage.append("Function " + identifier.toString() + " has statements after return\n");
      }
    }

     return errorMessage.toString();
  }

  public void setReturnType(TypeNode returnType) {
    //bodyStatement.setReturnType(returnType.getType());
  }
}