package AbstractSyntaxTree.expression;

import InternalRepresentation.Enums.LdrType;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.InternalState;
import InternalRepresentation.Register;
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
    Register currDestination = internalState.getFreeRegister();
    internalState.setPrevResult(currDestination);
    internalState.addInstruction(new LdrInstruction(LdrType.LDR, currDestination, 0));
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