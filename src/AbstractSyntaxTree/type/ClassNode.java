package AbstractSyntaxTree.type;

import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ClassNode implements TypeNode {

  private final IdentifierNode className;
  private final List<AttributeNode> attributes;
  private final List<ConstructorNode> constructors;
  private final List<FunctionNode> methods;

  public ClassNode(IdentifierNode className, List<AttributeNode> attributes,
      List<ConstructorNode> constructors, List<FunctionNode> methods) {
    this.className = className;
    this.attributes = attributes;
    this.constructors = constructors;
    this.methods = methods;
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
