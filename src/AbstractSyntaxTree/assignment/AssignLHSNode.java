package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public abstract class AssignLHSNode implements ASTNode {

  /* line:               Line number of node. Used for error messages
   * charPositionInLine: Character position of node in the line. Used for error messages */
  private final int line;
  private final int charPositionInLine;

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
}