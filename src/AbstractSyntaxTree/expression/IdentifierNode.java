package AbstractSyntaxTree.expression;

import InternalRepresentation.Enums.LdrType;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;

import java.util.List;

import static InternalRepresentation.Enums.LdrType.LDR;
import static InternalRepresentation.Enums.LdrType.LDRSB;

public class IdentifierNode extends ExpressionNode {

  private final static int BYTE_SIZE = 1;
  /* value: String representing the identifier of this node */
  private final String identifier;
  private SymbolTable currSymTable = null;

  public IdentifierNode(int line, int charPositionInLine, String identifier) {
    super(line, charPositionInLine);
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;

    /* Check that the identifier has been declared as either a ParameterId or VariableId.
     * FunctionId identifiers do not call this function */
    Identifier id = symbolTable.lookupAll(identifier);

    if (id == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Identifier '" + identifier + "' has not been declared.");
    } else if (!(id instanceof VariableId) && !(id instanceof ParameterId)) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Identifier '" + identifier + "' is referenced incorrectly."
                            + " Expected: VARIABLE IDENTIFIER, PARAMETER IDENTIFIER");
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    // get offset from symbolTable of variable and store that in available reg
    if (currSymTable == null) {
      return;
    }
    Identifier id = currSymTable.lookupAll(identifier);
    if (id == null) {
      return;
    }
    int offset = currSymTable.getOffset(getIdentifier());
    Register reg = internalState.peekFreeRegister();
    LdrType ldrInstr = (getType(currSymTable).getSize() == BYTE_SIZE) ? LDRSB : LDR;
    internalState.addInstruction(new LdrInstruction(ldrInstr, reg, Register.SP, offset));
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    /* Check whether identifier is declared as a VariableId, ParameterId or FunctionId
     * and return the type if so */
    Identifier id = symbolTable.lookupAll(identifier);

    if (id instanceof VariableId) {
      return id.getType();
    } else if (id instanceof ParameterId) {
      return id.getType();
    }

    /* Add '*' to search for a FunctionId */
    id = symbolTable.lookupAll("*" + identifier);
    if (id instanceof FunctionId) {
      return id.getType();
    }

    return null;
  }

  @Override
  public String toString() {
    return identifier;
  }
}