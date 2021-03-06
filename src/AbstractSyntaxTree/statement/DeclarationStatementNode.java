package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.TypeNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;
import AbstractSyntaxTree.expression.ArrayElemNode;
import SemanticAnalysis.*;

import java.util.List;

public class DeclarationStatementNode extends StatementNode {

  /* type:         TypeNode corresponding to the type of the new variable
   * identifier:   Name identifier given to the new variable
   * assignment:   AssignRHSNode corresponding to the value assigned to the new variable */
  private final TypeNode type;
  private final IdentifierNode identifier;
  private final AssignRHSNode assignment;

  public DeclarationStatementNode(TypeNode type, IdentifierNode identifier,
                                  AssignRHSNode assignment) {
    this.type = type;
    this.identifier = identifier;
    this.assignment = assignment;
  }

  public VariableId getIdentifierVar() {
    return new VariableId(identifier, type.getType());
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* If the assignment's type needs to be inferred, set the type to the declared type */
    DataTypeId declaredType = type.getType();
    DataTypeId assignedType;

    if (assignment.isUnsetParamId(symbolTable)) {
      ParameterId assignParam = assignment.getParamId(symbolTable);
      assignParam.setType(declaredType);
    }

    if (assignment instanceof ArrayElemNode) {
      ArrayElemNode arrElemNode = (ArrayElemNode) assignment;
      arrElemNode.setArrayElemBaseType(symbolTable, declaredType);
    }

    /* Check whether identifier has been previously declared as another variable in the current scope.
     * If not, add a new VariableId to the symbol table under identifier */
    if (symbolTable.lookup(identifier.getIdentifier()) != null) {
      errorMessages.add(new SemanticError(identifier.getLine(), identifier.getCharPositionInLine(),
          "Identifier '" + identifier.getIdentifier()
              + "' has already been declared in the same scope."));

    } else {
      symbolTable.add(identifier.getIdentifier(),
          new VariableId(identifier, type.getType()));
    }

    declaredType = type.getType();

    if (declaredType == null) {
      errorMessages.add(new SemanticError(assignment.getLine(),(assignment.getCharPositionInLine()),
              "Could not resolve type of '" + identifier.getIdentifier() + "'."));
      return;
    }

    /* Check that the expected (declared) type and the type of assignment
     * can be resolved and match */
    assignedType = getTypeOfOverloadFunc(symbolTable, errorMessages, declaredType, assignment);

    if (assignedType == null) {
      errorMessages.add(new SemanticError(assignment.getLine(), assignment.getCharPositionInLine(),
          "Could not resolve type of '" + assignment.toString() + "'."
          + " Expected: " + declaredType));

    } else if (!declaredType.equals(assignedType) && !stringToCharArray(declaredType,
        assignedType)) {
      errorMessages.add(new SemanticError(assignment.getLine(), assignment.getCharPositionInLine(),
          "Assignment type does not match declared type for '"
              + identifier.getIdentifier() + "'."
              + " Expected: " + declaredType + " Actual: " + assignedType));
    }

    /* Recursively call semanticAnalysis on stored nodes */
    identifier.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    assignment.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitDeclarationStatementNode(
        internalState, assignment, type, identifier, getCurrSymTable());
  }
}