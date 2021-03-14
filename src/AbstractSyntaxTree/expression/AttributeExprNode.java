package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.type.AttributeNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class AttributeExprNode extends ExpressionNode {

  private IdentifierNode objectName;
  private AttributeNode attributeNode;

  public AttributeExprNode(int line, int charPositionInLine) {
    super(line, charPositionInLine);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return null;
  }

  @Override
  public String toString() {
    return null;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }
}
