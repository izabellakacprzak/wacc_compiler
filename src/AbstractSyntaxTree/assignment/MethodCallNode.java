package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;

public class MethodCallNode extends CallNode{

  private final IdentifierNode objectName;
  private final IdentifierNode methodName;
  private final List<ExpressionNode> arguments;

  public MethodCallNode(int line, int charPositionInLine, IdentifierNode objectName,
      IdentifierNode methodName, List<ExpressionNode> arguments) {
    super(line, charPositionInLine);
    this.objectName = objectName;
    this.methodName = methodName;
    this.arguments = arguments;
  }

  public Identifier getIdentifier(SymbolTable symbolTable) {
    return symbolTable.lookup("*" + methodName.getIdentifier());
  }

  public SymbolTable getClassSymTable(SymbolTable symbolTable) {
    Identifier object = symbolTable.lookupAll(objectName.getIdentifier());
    if (!(object instanceof ObjectId)) {
      return null;
    }

    ObjectId objectId = (ObjectId) object;
    DataTypeId classType = objectId.getType();
    if (!(classType instanceof ClassType)) {
      return null;
    }

    return  ((ClassType) classType).getFields().get(0).getCurrSymTable();
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    SymbolTable classTable = getClassSymTable(symbolTable);

    if(classTable == null) {
      return null;
    }

    Identifier functionId = classTable.lookupAll("*" + methodName.getIdentifier());
    FunctionId function = (FunctionId) functionId;

    if (function == null) {
      return null;
    }

    return function.getType();
  }

  public List<DataTypeId> getOverloadType(SymbolTable symbolTable) {
    SymbolTable classTable = getClassSymTable(symbolTable);

    if(classTable == null) {
      return null;
    }

    Identifier functionId = classTable.lookupAll("*" + methodName.getIdentifier());
    return getOverloadDataTypeIds(symbolTable, (OverloadFuncId) functionId, arguments);
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("method: ").append(methodName.getIdentifier()).append('(');

    return getString(str, arguments);
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);

    /* Check if object has been declared and is in fact an object */
    objectName.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    Identifier object = symbolTable.lookupAll(objectName.getIdentifier());
    if (!(object instanceof ObjectId)) {
      errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
              "Cannot call a method on a non-object."   + " Expected: OBJECT TYPE "
              + " Actual: " + object));
    } else {
      ObjectId objectId = (ObjectId) object;
      DataTypeId classType = objectId.getType();
      if (!(classType instanceof ClassType)) {
        errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
                "Could not properly resolve object type."   + " Expected: CLASS TYPE "
                + " Actual: " + classType));
      } else {
        SymbolTable classTable = ((ClassType) classType).getFields().get(0).getCurrSymTable();
        Identifier functionId = classTable.lookup("*" + methodName.getIdentifier());

        /* Check if method has been declared in the appropriate class */
        if (functionId == null) {
          errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
                  "Could not find method with signature '"  + methodName.getIdentifier() + "' declared in class "
                  + ((ClassType) classType).getClassName()));
        } else {
          super.semAnalyseFunctionArgs(symbolTable, errorMessages, methodName, arguments,
              functionId, uncheckedNodes, firstCheck);
        }
      }
    }
    /* Semantically analyse all arguments */
    for (ExpressionNode arg : arguments) {
      arg.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }
}