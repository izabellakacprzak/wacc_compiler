package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;

public abstract class AssignLHSNode implements ASTNode {

  /* INT_BYTES_SIZE: size of an int in bytes */
  protected final static int INT_BYTES_SIZE = 4;

  /* line:               Line number of node. Used for error messages
   * charPositionInLine: Character position of node in the line. Used for error messages
   * currSymTable: the node's symbol table of identifiers it can access */
  private final int line;
  private final int charPositionInLine;
  private SymbolTable currSymTable = null;

  protected AssignLHSNode(int line, int charPositionInLine) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
  }

  /* Returns the DataTypeId corresponding to the AssignLHSNode's DataType or return DataType */
  public abstract DataTypeId getType(SymbolTable symbolTable);

  public int getLine() {
    return line;
  }

  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  /* Returns a ParameterId if one exists for this IdentifierNode, otherwise null */
  public ParameterId getParamId(SymbolTable symbolTable) {
    if (!(this instanceof IdentifierNode)) {
      return null;
    }

    IdentifierNode idNode = (IdentifierNode) this;
    Identifier identifierId = symbolTable.lookupAll(idNode.getIdentifier());

    ParameterId param = null;
    if (identifierId instanceof ParameterId) {
      param = (ParameterId) identifierId;
    }

    return param;
  }

  public boolean isUnsetParamId(SymbolTable symbolTable) {
    ParameterId param = getParamId(symbolTable);
    if (!(this instanceof IdentifierNode)) {
      return false;
    }
    return !(param == null) && param.getType() == null;
  }

  public boolean isUnsetParamArray(SymbolTable symbolTable) {
    ParameterId param = this.getParamId(symbolTable);

    if (param == null) {
      return false;
    }

    return param.isUnsetArray();
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

  /* All AssignLHSNodes must Override toString for use in error messages */
  @Override
  public abstract String toString();
}