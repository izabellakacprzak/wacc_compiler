package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;

public class ConstructorNode implements TypeNode{

  private final IdentifierNode name;
  private final ParamListNode parameters;
  private final StatementNode bodyStatement;

  private SymbolTable currSymTable = null;

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
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);

    /* Get class name and search it up in symbol table */
    String className = "class_" + name.getIdentifier();
    Identifier classId = symbolTable.lookupAll(className);

    /* Check if name matches class name */
    if (classId == null) {
      errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
              "Constructor name '" + name.getIdentifier()
                  + "' does not match declared class."));
    } else if (!(classId instanceof ClassType)) {
      errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
              "Constructor '" + name.getIdentifier()
                  + "' can only be declared for a class."));
    } else {
      ClassType classIdentifier = (ClassType) classId;
      ConstructorId constructor = new ConstructorId(name, parameters.getIdentifiers(symbolTable));

      /* Check if such constructor already exists, if not add to list of constructors in symbol table */
      if (!classIdentifier.addConstructor(constructor)){
        errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
                "Constructor '" + constructor.toString() + "' has already been declared."));
      }
    }

    parameters.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    bodyStatement.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
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
