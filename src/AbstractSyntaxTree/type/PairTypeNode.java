package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class PairTypeNode implements TypeNode {

    private final TypeNode fstType;
    private final TypeNode sndType;

    public PairTypeNode(TypeNode fstType, TypeNode sndType) {
        this.fstType = fstType;
        this.sndType = sndType;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
        fstType.semanticAnalysis(symbolTable, errorMessages);
        sndType.semanticAnalysis(symbolTable, errorMessages);
    }

    @Override
    public Identifier getIdentifier(SymbolTable parentSymbolTable) {
        return new PairType((DataTypeId) fstType.getIdentifier(parentSymbolTable),
            (DataTypeId) sndType.getIdentifier(parentSymbolTable));
    }
}