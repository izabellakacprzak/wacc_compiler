package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.AssignLHSNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ReadStatementNode extends StatementNode {

  /* assignment:   AssignLHSNode corresponding to the IdentifierNode, ArrayElemNode
   *                 or PairElemNode 'read' was called with */
  private final AssignLHSNode assignment;

  public ReadStatementNode(AssignLHSNode assignment) {
    this.assignment = assignment;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Recursively call semanticAnalysis on assignment node */
    assignment.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that the type of assignment is an INT or a CHAR */
    DataTypeId assignmentType = assignment.getType(symbolTable);

    if (assignmentType == null) {
      errorMessages.add(new SemanticError(assignment.getLine(), assignment.getCharPositionInLine(),
          "Could not resolve type for '" + assignment + "."
              + " Expected: INT, CHAR"));

    } else if (!assignmentType.equals(new BaseType(BaseType.Type.INT)) &&
        !assignmentType.equals(new BaseType(BaseType.Type.CHAR))) {
      errorMessages.add(new SemanticError(assignment.getLine(), assignment.getCharPositionInLine(),
          "Incompatible type for 'read' statement."
              + " Expected: INT, CHAR Actual: " + assignmentType));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
        visitReadStatementNode(internalState, assignment, getCurrSymTable());
  }

}