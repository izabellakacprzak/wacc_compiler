package AbstractSyntaxTree;

import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ProgramNode implements ASTNode {
    private final StatementNode statementNode;
    private final List<FunctionNode> functionNodes;

    public ProgramNode(StatementNode statementNode, List<FunctionNode> functionNodes) {
        this.statementNode = statementNode;
        this.functionNodes = functionNodes;
    }

    @Override
    public void semanticAnalysis(SymbolTable topSymbolTable, List<String> errorMessages) {
        // go through list of nodes, for each new function create a symbol table
        // and perform the semantic analysis on it, passing the newly created sym table

        for (FunctionNode func : functionNodes) {
            // check if declared
            // if not add to top table
            if (topSymbolTable.lookupAll(func.getName()) == null) {
                // function is defined - add error message and exit
                errorMessages.add("Attempt at redefining already existing function " + func.getName());
                return;
            }

            topSymbolTable.add(func.getName(), func.createIdentifier(topSymbolTable));
        }


        for (FunctionNode func : functionNodes) {
            func.semanticAnalysis(topSymbolTable, errorMessages);
        }

        //do semantic analysis on the statement node
        statementNode.semanticAnalysis(topSymbolTable, errorMessages);
    }
}