package AbstractSyntaxTree.expression;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class PairLiterExprNode extends ExpressionNode {

  private SymbolTable currSymTable = null;

  public PairLiterExprNode(int line, int charPositionInLine) {
    super(line, charPositionInLine);
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
   internalState.getCodeGenVisitor().visitPairLiterExprNode(internalState);
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
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