package AbstractSyntaxTree.expression;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class CharLiterExprNode extends ExpressionNode {

  /* value: char representing the value of this node's CHAR literal */
  private final char value;

  public CharLiterExprNode(int line, int charPositionInLine, char value) {
    super(line, charPositionInLine);
    this.value = value;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitCharLiterExprNode(internalState, value);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(Type.CHAR);
  }

  @Override
  public String toString() {
    return Character.toString(value);
  }
}