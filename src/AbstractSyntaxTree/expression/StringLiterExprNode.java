package AbstractSyntaxTree.expression;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class StringLiterExprNode extends ExpressionNode {

  /* value: String representing the value of this node's STRING literal */
  private final String value;

  public StringLiterExprNode(int line, int charPositionInLine, String value) {
    super(line, charPositionInLine);
    this.value = value.substring(1, value.length() - 1);
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitStringLiterNode(internalState, value);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    return new BaseType(BaseType.Type.STRING);
  }

  @Override
  public String toString() {
    return value;
  }
}