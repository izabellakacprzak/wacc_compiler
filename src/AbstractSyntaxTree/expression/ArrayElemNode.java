package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.assignment.AssignLHSNode;

import java.util.List;

public class ArrayElemNode extends ExpressionNode implements AssignLHSNode {
    private final IdentifierNode identifier;
    private final List<ExpressionNode> expressions;

    public ArrayElemNode(IdentifierNode identifier, List<ExpressionNode> expressions){
      this.identifier = identifier;
      this.expressions = expressions;
    }
}