package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;

public class AssignVarNode extends StatementNode {
    private final AssignLHSNode left;
    private final AssignRHSNode right;

    public AssignVarNode(AssignLHSNode left, AssignRHSNode right) {
        this.left = left;
        this.right = right;
    }
}