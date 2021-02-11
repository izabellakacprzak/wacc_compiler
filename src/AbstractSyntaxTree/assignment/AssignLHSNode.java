package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;

public abstract class AssignLHSNode implements ASTNode {

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

  public abstract DataTypeId getType(SymbolTable symbolTable);

  @Override
  public abstract String toString();
}