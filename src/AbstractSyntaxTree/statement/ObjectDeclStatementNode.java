package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ObjectDeclStatementNode extends StatementNode {

  private final IdentifierNode className;
  private final IdentifierNode objectName;
  private final List<ExpressionNode> expressions;

  public ObjectDeclStatementNode(IdentifierNode className, IdentifierNode objectName,
      List<ExpressionNode> expressions) {
    this.className = className;
    this.objectName = objectName;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }
}
