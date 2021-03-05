package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public abstract class AssignLHSNode implements ASTNode {

  /* INT_BYTES_SIZE: size of an int in bytes */
  protected final static int INT_BYTES_SIZE = 4;

  /* line:               Line number of node. Used for error messages
   * charPositionInLine: Character position of node in the line. Used for error messages
   * currSymTable: the node's symbol table of identifiers it can access */
  private final int line;
  private final int charPositionInLine;
  protected SymbolTable currSymTable = null;

  protected AssignLHSNode(int line, int charPositionInLine) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
  }

  public int getLine() {
    return line;
  }

  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  /* Returns the DataTypeId corresponding to the AssignLHSNode's DataType or return DataType */
  public abstract DataTypeId getType(SymbolTable symbolTable);

  /* All AssignLHSNodes must Override toString for use in error messages */
  @Override
  public abstract String toString();

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }
}