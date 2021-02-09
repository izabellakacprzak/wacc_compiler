package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ParamListNode implements ASTNode {
    private final List<IdentifierNode> identifiers;
    private final List<TypeNode> types;

    public ParamListNode(List<IdentifierNode> identifiers, List<TypeNode> types) {
        this.identifiers = identifiers;
        this.types = types;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }

    public List<ParameterId> createIdentifiers(SymbolTable parentSymbolTable) {
        List<ParameterId> paramIds = new ArrayList<>();
        for (int i = 0; i < identifiers.size(); i++) {
            paramIds.add(new ParameterId(identifiers.get(i),
                (DataTypeId) types.get(i).createIdentifier(parentSymbolTable)));
        }
        return paramIds;
    }
    // Might turn out redundant?
    public List<DataTypeId> getParamTypes() {
        List<DataTypeId> params = new ArrayList<>();
        for (TypeNode curr : types) {
            params.add(curr.getType());
        }
        return params;
    }
}
