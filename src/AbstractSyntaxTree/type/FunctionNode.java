package AbstractSyntaxTree.type;

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

  private SymbolTable currSymTable;

  public FunctionNode(TypeNode returnType, IdentifierNode identifier,
      ParamListNode params, StatementNode bodyStatement) {
    this.returnType = returnType;
    this.identifier = identifier;
    this.params = params;
    this.bodyStatement = bodyStatement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    returnType.semanticAnalysis(currSymTable, errorMessages);
    identifier.semanticAnalysis(currSymTable, errorMessages);
    params.semanticAnalysis(currSymTable, errorMessages);
    bodyStatement.semanticAnalysis(currSymTable, errorMessages);
  }

  public String getName() {
    return identifier.getIdentifier();
  }

  @Override
  public Identifier getIdentifier(SymbolTable parentSymTable) {
    currSymTable = new SymbolTable(parentSymTable);
    return new FunctionId(this, (DataTypeId) returnType.getIdentifier(parentSymTable),
        params.getIdentifiers(parentSymTable), currSymTable);
  }
}