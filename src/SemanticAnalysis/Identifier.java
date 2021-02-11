package SemanticAnalysis;

import AbstractSyntaxTree.ASTNode;

public abstract class Identifier {

  private final ASTNode node;

  public Identifier(ASTNode node) {
    this.node = node;
  }

  public ASTNode getNode() {
    return node;
  }

  @Override
  public abstract String toString();
}