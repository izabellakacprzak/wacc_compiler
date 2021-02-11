package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ReadStatementNode extends StatementNode {

  private final AssignLHSNode left;

  public ReadStatementNode(AssignLHSNode left) {
    this.left = left;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    left.semanticAnalysis(symbolTable, errorMessages);

    DataTypeId leftType = left.getType(symbolTable);

    if (leftType == null) {
      errorMessages.add(left.getLine() + ":" + left.getCharPositionInLine()
          + " Could not resolve type for '" + left + "."
          + " Expected: INT, CHAR");

    } else if (!leftType.equals(new BaseType(BaseType.Type.INT)) &&
        !leftType.equals(new BaseType(BaseType.Type.CHAR))) {
      errorMessages.add(left.getLine() + ":" + left.getCharPositionInLine()
          + " Incompatible type for 'read' statement."
          + " Expected: INT, CHAR Actual: " + leftType);
    }
  }
}