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
    if (fstType != null) {
      fstType.semanticAnalysis(symbolTable, errorMessages);
    }
    if (sndType != null) {
      sndType.semanticAnalysis(symbolTable, errorMessages);
    }
  }

  @Override
  public Identifier getIdentifier(SymbolTable parentSymbolTable) {
    return new PairType((DataTypeId) fstType.getIdentifier(parentSymbolTable),
        (DataTypeId) sndType.getIdentifier(parentSymbolTable));
  }

  private DataTypeId getPairElemNode(TypeNode elem) {

    if (elem == null) {
      return null;
    }
    return elem.getType();
  }

  @Override
  public DataTypeId getType() {
    return new PairType(getPairElemNode(fstType), getPairElemNode(sndType));
  }
}