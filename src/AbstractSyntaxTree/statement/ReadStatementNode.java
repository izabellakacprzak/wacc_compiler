package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReadStatementNode extends StatementNode {

  /* assignment:  AssignLHSNode corresponding to the IdentifierNode, ArrayElemNode
   *                or PairElemNode 'read' was called with */
  private final AssignLHSNode assignment;
  private SymbolTable currSymTable = null;

  public ReadStatementNode(AssignLHSNode assignment) {
    this.assignment = assignment;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on assignment node */
    assignment.semanticAnalysis(symbolTable, errorMessages);

    /* Check that the type of assignment is an INT or a CHAR */
    DataTypeId assignmentType = assignment.getType(symbolTable);

    if (assignmentType == null) {
      errorMessages.add(assignment.getLine() + ":" + assignment.getCharPositionInLine()
          + " Could not resolve type for '" + assignment + "."
          + " Expected: INT, CHAR");

    } else if (!assignmentType.equals(new BaseType(BaseType.Type.INT)) &&
        !assignmentType.equals(new BaseType(BaseType.Type.CHAR))) {
      errorMessages.add(assignment.getLine() + ":" + assignment.getCharPositionInLine()
          + " Incompatible type for 'read' statement."
          + " Expected: INT, CHAR Actual: " + assignmentType);
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }
}