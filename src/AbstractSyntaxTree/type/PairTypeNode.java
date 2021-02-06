package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class PairTypeNode implements TypeNode {
    private TypeNode fstType;
    private TypeNode sndType;

    public PairTypeNode(TypeNode fstType, TypeNode sndType) {
        this.fstType = fstType;
        this.sndType = sndType;
    }

    @Override
    public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

    }

    @Override
    public Identifier createIdentifier(SymbolTable parentSymbolTable) {
        return new PairType(this, (DataTypeId) fstType.createIdentifier(parentSymbolTable),
                (DataTypeId) sndType.createIdentifier(parentSymbolTable));
    }
}