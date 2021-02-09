package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;

import java.util.List;

public class ArrayElemNode implements ExpressionNode {

  private final IdentifierNode identifier;
  private final List<ExpressionNode> expressions;

  public ArrayElemNode(IdentifierNode identifier, List<ExpressionNode> expressions) {
    this.identifier = identifier;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    VariableId arrayName = (VariableId) symbolTable.lookupAll(identifier.getIdentifier());


    return null;
  }
}