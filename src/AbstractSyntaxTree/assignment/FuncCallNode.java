package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.FunctionNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FuncCallNode implements AssignRHSNode {

  private final IdentifierNode identifier;
  private final List<ExpressionNode> arguments;

  public FuncCallNode(IdentifierNode identifier, List<ExpressionNode> arguments) {
    this.identifier = identifier;
    this.arguments = arguments;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    Identifier functionId = symbolTable.lookupAll(identifier.getIdentifier());
    if (functionId == null) {
      errorMessages.add("Function " + identifier.getIdentifier() + " has not been declared.");
      // TODO: Do we check the arguments? plain return?
    }

    if (!(functionId instanceof FunctionId)) {
      errorMessages.add("Attempt at calling " + identifier.getIdentifier() + " as a function.");
      // TODO: Do we check the arguments? plain return?
    }

  }


  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    FunctionId function = (FunctionId) symbolTable.lookupAll(identifier.getIdentifier());
    return function.getReturnType();
  }
}