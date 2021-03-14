package AbstractSyntaxTree.type;

import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ClassNode implements TypeNode {

  private IdentifierNode className;
  private List<AttributeNode> attributes;
  private List<ConstructorNode> constructors;
  private List<FunctionNode> methods;

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
