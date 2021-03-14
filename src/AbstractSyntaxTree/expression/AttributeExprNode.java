package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.type.AttributeNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class AttributeExprNode extends ExpressionNode {

  private IdentifierNode objectName;
  private IdentifierNode attributeName;

  public AttributeExprNode(int line, int charPositionInLine, IdentifierNode objectName,
      IdentifierNode attributeName) {
    super(line, charPositionInLine);
    this.objectName = objectName;
    this.attributeName = attributeName;
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
