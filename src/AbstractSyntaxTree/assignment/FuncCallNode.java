package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.FunctionNode;
import java.util.List;

public class FuncCallNode implements AssignRHSNode {
    private IdentifierNode identifier;
    private FunctionNode function;
    private List<ExpressionNode> arguments;

    public FuncCallNode(IdentifierNode identifier, FunctionNode function, List<ExpressionNode> arguments) {
        this.identifier = identifier;
        this.function = function;
        this.arguments = arguments;
    }
}