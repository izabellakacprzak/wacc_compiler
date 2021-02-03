package AbstractSyntaxTree.type;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;

public class FunctionNode extends TypeNode {
    private TypeNode returnType;
    private IdentifierNode identifier;
    private ParamListNode params;
    private StatementNode bodyStatement;

    public FunctionNode(TypeNode returnType, IdentifierNode identifier,
        ParamListNode params, StatementNode bodyStatement) {
            this.returnType = returnType;
            this.identifier = identifier;
            this.params = params;
            this.bodyStatement = bodyStatement;
    }
}