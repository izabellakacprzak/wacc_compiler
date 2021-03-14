package AbstractSyntaxTree.type;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ConstructorNode implements TypeNode{

  private final IdentifierNode name;
  private final ParamListNode parameters;
  private final StatementNode bodyStatement;

  public ConstructorNode(IdentifierNode name, ParamListNode parameters,
      StatementNode bodyStatement) {
    this.name = name;
    this.parameters = parameters;
    this.bodyStatement = bodyStatement;
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return null;
  }

  @Override
  public DataTypeId getType() {
    return null;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Check if name matches class name */
    // TODO: WRITE TEST CASE WHICH CHECKS FOR CONSTRUCTOR A IN CLASS B FOR TWO CLASSES DECLARED
    /* Get class name and search it up in symbol table */
    /* Check if such constructor already exists, if not add to list of constructors in symbol table */
    parameters.semanticAnalysis(symbolTable, errorMessages);
    bodyStatement.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {

  }

  @Override
  public SymbolTable getCurrSymTable() {
    return null;
  }
}
