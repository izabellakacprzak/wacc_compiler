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
    if ((this instanceof IdentifierNode)) {
      IdentifierNode idNode = (IdentifierNode) this;
      ParameterId param = null;

      Identifier identifierId = symbolTable.lookupAll(idNode.getIdentifier());

      if (identifierId instanceof ParameterId) {
        param = (ParameterId) identifierId;
      }

      return param;
    } else if (this instanceof ArrayElemNode) {
      ArrayElemNode arrNode = (ArrayElemNode) this;
      ParameterId param = null;

      Identifier identifierId = symbolTable.lookupAll(arrNode.getIdentifier().getIdentifier());

      if (identifierId instanceof ParameterId) {
        param = (ParameterId) identifierId;
      }
      return param;
    }
    return null;
  }

  public boolean isUnsetParamId(SymbolTable symbolTable) {
    ParameterId param = getParamId(symbolTable);
    if ((this instanceof IdentifierNode)) {
    return !(param == null) && param.getType() == null;
  }
  else if(this instanceof ArrayElemNode){
      return !(param == null) && ((ArrayElemNode)this).getType(symbolTable) == null;
  }
  return false;
  }
}