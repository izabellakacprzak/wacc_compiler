package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PairLiterExprNode extends ExpressionNode {

  public PairLiterExprNode(int line, int charPositionInLine) {
    super(line, charPositionInLine);
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new PairType(null, null);
  }

  @Override
  public String toString() {
    return "null";
  }
}