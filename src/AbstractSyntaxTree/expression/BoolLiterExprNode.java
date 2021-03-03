package AbstractSyntaxTree.expression;

import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class BoolLiterExprNode extends ExpressionNode {

  /* value: boolean representing the value of this node's BOOL literal */
  private final boolean value;
  private SymbolTable currSymTable = null;

  public BoolLiterExprNode(int line, int charPositionInLine, boolean value) {
    super(line, charPositionInLine);
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    int intValue = value? 1 : 0;
    Register currDestination = internalState.peekFreeRegister();
    internalState.addInstruction(new MovInstruction(currDestination, intValue));
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(BaseType.Type.BOOL);
  }

  @Override
  public String toString() {
    return Boolean.toString(value);
  }
}