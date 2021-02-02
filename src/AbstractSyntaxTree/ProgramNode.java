public class ProgramNode implements ASTNode {
    private StatementNode statementNode;
    private List<FunctionNode> functionNodes;

    public ASTProgramNode(StatementNode statementNode, List<FunctionNode> functionNodes) {
        this.statementNode = statementNode;
        this.functionNodes = functionNodes;
    }
}