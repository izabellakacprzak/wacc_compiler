package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class AssignVarNode implements StatementNode {

  private final AssignLHSNode left;
  private final AssignRHSNode right;

  public AssignVarNode(AssignLHSNode left, AssignRHSNode right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    left.semanticAnalysis(symbolTable, errorMessages);
    right.semanticAnalysis(symbolTable, errorMessages);

    // get type of left node
    // get type of right node

    // override type equals and compare the two nodes
    // if type differs - "Assignment to: " + left + "must be of type " + left.type +
    // " not " + right.type

  }
}