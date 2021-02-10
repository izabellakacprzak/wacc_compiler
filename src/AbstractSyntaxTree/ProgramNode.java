package AbstractSyntaxTree;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import java.util.ArrayList;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.SymbolTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramNode implements ASTNode {

    private final StatementNode statementNode;
    private final List<FunctionNode> functionNodes;
    private final Map<FunctionNode, SymbolTable> funcTables = new HashMap<>();
    private List<String> syntaxErrors;


    public ProgramNode(StatementNode statementNode, List<FunctionNode> functionNodes) {
        this.statementNode = statementNode;
        this.functionNodes = functionNodes;
        syntaxErrors = new ArrayList<>();
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

                funcTables.put(func, new SymbolTable(topSymbolTable));
            } else {
                FunctionId identifier = (FunctionId) func.getIdentifier(topSymbolTable);

                funcTables.put(func, identifier.getSymTable());
                topSymbolTable.add(func.getName(), identifier);
            }
        }

        for (FunctionNode func : functionNodes) {
            func.semanticAnalysis(funcTables.get(func), errorMessages);

        }

        //do semantic analysis on the statement node with new scope
        statementNode.semanticAnalysis(topSymbolTable, errorMessages);
    }

    public List<String> checkSyntaxErrors() {
        String error;
        for (FunctionNode f : functionNodes) {
            error = f.checkSyntaxErrors();
            if (!error.isEmpty()) {
                syntaxErrors.add(error);
            }
        }
        return syntaxErrors;
    }
}