package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.ClassNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.ArrayList;
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

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return null;
  }

  @Override
  /* TODO: REDO */
  public String toString() {
    return null;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Check if object has been declared and is in fact an object */
    objectName.semanticAnalysis(symbolTable, errorMessages);
    Identifier object = symbolTable.lookupAll(objectName.getIdentifier());
    if (!(object instanceof ObjectId)) {
      errorMessages.add(objectName.getLine() + ":" + objectName.getCharPositionInLine() +
              "Cannot call a method on a non-object."   + " Expected: OBJECT TYPE "
              + " Actual: " + object);
    } else {
      ObjectId objectId = (ObjectId) object;
      DataTypeId classType = objectId.getType();
      if (!(classType instanceof ClassType)) {
        errorMessages.add(objectName.getLine() + ":" + objectName.getCharPositionInLine() +
                "Could not properly resolve object type."   + " Expected: CLASS TYPE "
                + " Actual: " + classType);
      } else {
        SymbolTable classTable = ((ClassType) classType).getFields().get(0).getCurrSymTable();
        Identifier functionId = symbolTable.lookup("*" + methodName.getIdentifier());

        /* Check if method has been declared in the appropriate class */
        if (functionId == null) {
          errorMessages.add(objectName.getLine() + ":" + objectName.getCharPositionInLine() +
                  "Could not find method with signature '"  + methodName.getIdentifier() + "' declared in class "
                  + ((ClassType) classType).getClassName());
        } else {
          super.semAnalyseFunctionArgs(symbolTable, errorMessages, methodName, arguments, functionId);
        }
      }
    }
    /* Semantically analyse all arguments */
    for (ExpressionNode arg : arguments) {
      arg.semanticAnalysis(symbolTable, errorMessages);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }
}