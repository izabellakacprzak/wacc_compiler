package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.TypeNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectDeclStatementNode extends StatementNode {

  private final IdentifierNode className;
  private final IdentifierNode objectName;
  private final List<ExpressionNode> expressions;

  public ObjectDeclStatementNode(IdentifierNode className, IdentifierNode objectName,
      List<ExpressionNode> expressions) {
    this.className = className;
    this.objectName = objectName;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    /* Check if className is a valid class name */
    Identifier classId = symbolTable.lookupAll("class_" + className.getIdentifier());
    ObjectId newObject = null;

    if (!(classId instanceof ClassType)) {
      errorMessages.add(objectName.getLine() + ":" + objectName.getCharPositionInLine() +
              "Cannot declare object." + " Expected: CLASS TYPE "
              + " Actual: " + classId);
    } else {
      /* Try to find matching constructor for these parameter types */
      List<ConstructorId> constructors = ((ClassType) classId).getConstructors();

      /* Get types of all expressions */
      List<DataTypeId> exprTypes = expressions.stream().
              map(x -> x.getType(symbolTable)).
              collect(Collectors.toList());

      for (ConstructorId constructor : constructors) {
        List<DataTypeId> parameters = constructor.getParameterTypes();
        if (parameters.size() == exprTypes.size()) {
          int i;

          for (i = 0; i < parameters.size(); i++) {
            if(exprTypes.get(i) == null) {
              errorMessages.add(
                  expressions.get(i).getLine()
                      + ":"
                      + expressions.get(i).getCharPositionInLine()
                      + " Could not resolve type of parameter "
                      + (i + 1)
                      + " in '"
                      + className.getIdentifier()
                      + "' function."
                      + " Expected: "
                      + parameters.get(i).getType());
            } else if (!exprTypes.get(i).equals(parameters.get(i))) {
              break;
            }
          }

          if (i == parameters.size()) {
            newObject = new ObjectId(objectName, (ClassType) classId, constructor);
            break;
          }
        }
      }

      if (newObject == null) {
        errorMessages.add(objectName.getLine() + ":" + objectName.getCharPositionInLine() +
                "Could not find matching constructor for object '" + objectName.getIdentifier() + "'.");
      }
    }

    /* Check if objectName is being redeclared, if not make ObjectId */
    if (symbolTable.lookupAll(objectName.getIdentifier()) != null) {
      errorMessages.add(objectName.getLine() + ":" + objectName.getCharPositionInLine() +
              " Identifier '" + objectName.getIdentifier()
              + "' has already been declared in the same scope.");
    } else if (newObject != null){
      symbolTable.add(objectName.getIdentifier(), newObject);
    }

    for(ExpressionNode expr : expressions) {
      expr.semanticAnalysis(symbolTable, errorMessages);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }
}