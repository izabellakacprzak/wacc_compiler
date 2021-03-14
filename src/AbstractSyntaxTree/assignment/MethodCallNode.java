package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.ClassNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class MethodCallNode extends AssignRHSNode{

  private IdentifierNode objectName;
  private IdentifierNode methodName;
  private List<ExpressionNode> arguments;
  private ClassNode classNode;

  public MethodCallNode(int line, int charPositionInLine) {
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
