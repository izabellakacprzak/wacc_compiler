package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.TypeNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;
import java.util.List;

public class DeclarationStatementNode extends StatementNode {

  private final TypeNode type;
  private final IdentifierNode identifier;
  private final AssignRHSNode assignment;

  public DeclarationStatementNode(TypeNode type, IdentifierNode identifier,
                                  AssignRHSNode assignment) {
    this.type = type;
    this.identifier = identifier;
    this.assignment = assignment;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    if (symbolTable.lookupAll(identifier.getIdentifier()) != null) {
      errorMessages.add(identifier.getLine() + ":" + identifier.getCharPositionInLine()
              + " Variable with name " + identifier.getIdentifier() +
              " has already been declared in the same scope.");
    } else {
      symbolTable.add(identifier.getIdentifier(),
              new VariableId(identifier, type.getType()));
    }
    // potentially might be redundant
    identifier.semanticAnalysis(symbolTable, errorMessages);
    assignment.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId declaredType = type.getType();
    DataTypeId assignedType = assignment.getType(symbolTable);

    if (declaredType == null) {
      errorMessages.add(assignment.getLine() + ":" + assignment.getCharPositionInLine()
              + " Could not resolve type of " + identifier.getIdentifier());
    } else if (assignedType == null) {
      errorMessages.add(assignment.getLine() + ":" + assignment.getCharPositionInLine()
              + " Could not resolve type of " + assignment.toString());
    } else if (!declaredType.equals(assignedType)) {
      errorMessages.add(assignment.getLine() + ":" + assignment.getCharPositionInLine()
              + " Declaration to: " + identifier.getIdentifier() + "must be of type " +
              declaredType.toString() + " not " + assignedType.toString());
    }
  }
}