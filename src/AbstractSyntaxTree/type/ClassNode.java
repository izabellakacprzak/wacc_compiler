package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;

public class ClassNode implements TypeNode {

  private final IdentifierNode className;
  private final List<AttributeNode> attributes;
  private final List<ConstructorNode> constructors;
  private final List<FunctionNode> methods;
  private SymbolTable currSymTable = null;

  public ClassNode(IdentifierNode className, List<AttributeNode> attributes,
      List<ConstructorNode> constructors, List<FunctionNode> methods) {
    this.className = className;
    this.attributes = attributes;
    this.constructors = constructors;
    this.methods = methods;
  }

  public String getName() {
    return "class_" + className.getIdentifier();
  }

  /* Returns identifier without extra 'class_' characters */
  public IdentifierNode getIdentifierNode() {
    return className;
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return new ClassType(className, attributes);
  }

  @Override
  public DataTypeId getType() {
    return null;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    setCurrSymTable(symbolTable);

    for (AttributeNode attribute : attributes) {
      attribute.semanticAnalysis(symbolTable, errorMessages);
    }

    for (ConstructorNode constructor : constructors) {
      constructor.semanticAnalysis(symbolTable, errorMessages);
    }

    ProgramNode.overloadFunc(symbolTable, errorMessages, methods);
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}
