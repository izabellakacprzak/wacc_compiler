package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;

public class ClassNode implements TypeNode {

  /* className:     name of the class
   * attributes:    list of AttributeNodes representing class attributes
   * constructors:  list of ConstructorNodes representing class constructors
   * methods:       list of FunctionNodes representing class methods
   * currSymTable:  current symbol table */
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
    return "class*" + className.getIdentifier();
  }

  /* Returns identifier without extra 'class*' characters */
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
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);

    for (AttributeNode attribute : attributes) {
      attribute.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }

    for (ConstructorNode constructor : constructors) {
      constructor.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }

    ProgramNode.overloadFunc(symbolTable, errorMessages, methods, uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitClassNode(internalState, className, attributes,
        constructors, methods);
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
