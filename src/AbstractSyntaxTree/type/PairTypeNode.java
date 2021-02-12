package AbstractSyntaxTree.type;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PairTypeNode implements TypeNode {

  /* fstType:  TypeNode of the first element in the pair's type
   * sndType:  TypeNode of the second element in the pair's type */
  private final TypeNode fstType;
  private final TypeNode sndType;

  public PairTypeNode(TypeNode fstType, TypeNode sndType) {
    this.fstType = fstType;
    this.sndType = sndType;
  }

  /* Returns the corresponding DataTypeId of a pair elem,
   * returning null if the elem is null */
  private DataTypeId getPairElemNode(TypeNode elem) {
    if (elem == null) {
      return null;
    }

    return elem.getType();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on each type */
    if (fstType != null) {
      fstType.semanticAnalysis(symbolTable, errorMessages);
    }

    if (sndType != null) {
      sndType.semanticAnalysis(symbolTable, errorMessages);
    }
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return new PairType((DataTypeId) fstType.getIdentifier(symbolTable),
        (DataTypeId) sndType.getIdentifier(symbolTable));
  }

  @Override
  public DataTypeId getType() {
    return new PairType(getPairElemNode(fstType), getPairElemNode(sndType));
  }
}