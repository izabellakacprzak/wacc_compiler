package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ArrayTypeNode implements TypeNode {
    private final TypeNode type;

    public ArrayTypeNode(TypeNode type) {
        this.type = type;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    }

    @Override
    public Identifier createIdentifier(SymbolTable parentSymbolTable) {
        return new ArrayType(this, (DataTypeId) type.createIdentifier(parentSymbolTable));
    }
}