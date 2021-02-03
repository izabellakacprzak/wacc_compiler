package AbstractSyntaxTree.expression;

import java.util.List;

public class ArrayElemNode extends ExpressionNode {
    private final IdentifierNode identifier;
    private final List<ExpressionNode> expressions;

    public ArrayElemNode(IdentifierNode identifier, List<ExpressionNode> expressions){
      this.identifier = identifier;
      this.expressions = expressions;
    }
}