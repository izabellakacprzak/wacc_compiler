package AbstractSyntaxTree;

import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import java.util.ArrayList;
import java.util.List;

public class ProgramNode implements ASTNode {
    private StatementNode statementNode;
    private List<FunctionNode> functionNodes;
    private List<String> syntaxErrors;

    public ProgramNode(StatementNode statementNode, List<FunctionNode> functionNodes) {
        this.statementNode = statementNode;
        this.functionNodes = functionNodes;
        syntaxErrors = new ArrayList<>();
    }

    public List<String> checkSyntaxErrors(){
        String error;
        for(FunctionNode f : functionNodes) {
            error = f.checkSyntaxErrors();
            if(!error.isEmpty()) {
                syntaxErrors.add(error);
            }
        }
    return syntaxErrors;
    }
}