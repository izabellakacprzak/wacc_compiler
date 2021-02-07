package AbstractSyntaxTree;

import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import java.util.List;

public class ProgramNode implements ASTNode {
    private StatementNode statementNode;
    private List<FunctionNode> functionNodes;

    public ProgramNode(StatementNode statementNode, List<FunctionNode> functionNodes) {
        this.statementNode = statementNode;
        this.functionNodes = functionNodes;
    }

    public boolean checkSyntaxErrors(){
    return functionNodes.stream().anyMatch(FunctionNode::checkSyntaxErrors);
    }
}