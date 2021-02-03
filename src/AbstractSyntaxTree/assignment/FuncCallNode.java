package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import java.util.List;

public class FuncCallNode implements AssignRHSNode {
    private IdentifierNode identifier;
    private List<ExpressionNode> arguments;

    public FuncCallNode(IdentifierNode identifier, List<ExpressionNode> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }
}