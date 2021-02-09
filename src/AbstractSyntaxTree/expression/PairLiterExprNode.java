package AbstractSyntaxTree.expression;

import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PairLiterExprNode implements ExpressionNode {

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {

  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new PairType(null, null, null);
  }
}