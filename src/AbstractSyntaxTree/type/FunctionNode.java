package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class FunctionNode implements TypeNode {

  private final TypeNode returnType;
  private final IdentifierNode identifier;
  private final ParamListNode params;
  private final StatementNode bodyStatement;

  public FunctionNode(TypeNode returnType, IdentifierNode identifier,
      ParamListNode params, StatementNode bodyStatement) {
    this.returnType = returnType;
    this.identifier = identifier;
    this.params = params;
    this.bodyStatement = bodyStatement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
  }

  public String getName() {
    return identifier.getIdentifier();
  }

  @Override
  public Identifier createIdentifier(SymbolTable topSymbolTable) {
    SymbolTable childSymTable = new SymbolTable(topSymbolTable);
    return new FunctionId(this, (DataTypeId) returnType.createIdentifier(topSymbolTable),
        params.createIdentifiers(topSymbolTable), childSymTable);
  }
}