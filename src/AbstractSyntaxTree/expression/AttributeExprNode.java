package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ClassType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AttributeExprNode extends ExpressionNode {

  /* objectName:    identifier of the object to which the attribute belongs
   * attributeName: name of the attribute */
  private final IdentifierNode objectName;
  private final IdentifierNode attributeName;

  public AttributeExprNode(int line, int charPositionInLine, IdentifierNode objectName,
      IdentifierNode attributeName) {
    super(line, charPositionInLine);
    this.objectName = objectName;
    this.attributeName = attributeName;
  }

  public String getObjectName() {
    return objectName.getIdentifier();
  }

  public int getAttributeIndex(SymbolTable symbolTable) {
      ClassType classType =  (ClassType) objectName.getType(symbolTable);
      return classType.findIndexAttribute(attributeName.getIdentifier());
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    DataTypeId objectType = objectName.getType(symbolTable);
    if (!(objectType instanceof ClassType)) {
      return null;
    } else {
      ClassType classType = (ClassType) objectType;
      SymbolTable classSymbolTable = classType.getAttributes().get(0).getCurrSymTable();

      /* Check if such an attribute exists for this class */
      Identifier attribute = classSymbolTable.lookup("attr*" + attributeName.getIdentifier());
      if(attribute == null) {
        return null;
      } else {
        return attribute.getType();
      }
    }
  }


  @Override
  public String toString() {
    return objectName.toString() + "." + attributeName.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Check class of object */
    DataTypeId objectType = objectName.getType(symbolTable);
    if (!(objectType instanceof ClassType)) {
      errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
              "Cannot get attribute of a non-object."   + " Expected: CLASS TYPE "
              + " Actual: " + objectType));
    } else {
      ClassType classType = (ClassType) objectType;
      SymbolTable classSymbolTable = classType.getAttributes().get(0).getCurrSymTable();

      /* Check if such an attribute exists for this class */
      if(classSymbolTable.lookup("attr*" + attributeName.getIdentifier()) == null) {
        errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
                "Attribute with name '" + attributeName.getIdentifier() + "' has not been declared for class '"
                + classType.getClassName()
                + " Expected: [" + classType.getAttributes().stream().map(Objects::toString).collect(Collectors.toList())
                + "] Actual: " + attributeName.getIdentifier()));
      }
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitAttributeExprNode(internalState, getCurrSymTable(),
        objectName, this.getType(getCurrSymTable()), attributeName);
  }
}