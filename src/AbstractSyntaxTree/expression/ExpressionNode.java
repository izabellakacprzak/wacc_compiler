package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;

public abstract class ExpressionNode extends AssignRHSNode {

  public ExpressionNode(int line, int charPositionInLine) {
    super(line, charPositionInLine);
  }

  /* Returns the DataTypeId corresponding to the ExpressionNode's DataType
   * or the DataType it returns */
  public abstract DataTypeId getType(SymbolTable symbolTable);

  /* All ExpressionNodes must Override toString for use in error messages */
  @Override
  public abstract String toString();

  /* Returns a ParameterId if one exists for this IdentifierNode, otherwise null */
  public ParameterId getParamId(SymbolTable symbolTable) {
    if (!(this instanceof IdentifierNode)) {
      return null;
    }

    IdentifierNode idNode = (IdentifierNode) this;
    ParameterId param = null;

    Identifier identifierId = symbolTable.lookupAll(idNode.getIdentifier());

    if (identifierId instanceof ParameterId) {
      param = (ParameterId) identifierId;
    }

    return param;
  }

  public boolean isUnsetParamId(SymbolTable symbolTable) {
    ParameterId param = getParamId(symbolTable);

    return !(param == null) && param.getType() == null;
  }
}