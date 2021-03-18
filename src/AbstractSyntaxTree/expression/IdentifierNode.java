package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;

import java.util.List;

public class IdentifierNode extends ExpressionNode {

  /* value:        String representing the identifier of this node */
  private final String identifier;

  public IdentifierNode(int line, int charPositionInLine, String identifier) {
    super(line, charPositionInLine);
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Check that the identifier has been declared as either a ParameterId or VariableId.
     * FunctionId identifiers do not call this function */
    Identifier id = symbolTable.lookupAll(identifier);

    if (id == null) {
      id = symbolTable.lookupAll("attr*" + identifier);
      if (id == null) {
        errorMessages.add(
            new SemanticError(
                super.getLine(),
                super.getCharPositionInLine(),
                "Identifier '" + identifier + "' has not been declared."));
      }
    } else if (!(id instanceof VariableId) && !(id instanceof ParameterId)
            && !(id instanceof ObjectId)) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Identifier '" + identifier + "' is referenced incorrectly."
          + " Expected: VARIABLE IDENTIFIER, PARAMETER IDENTIFIER"));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    DataTypeId type = getType(getCurrSymTable());
    internalState.getCodeGenVisitor().
        visitIdentifierNode(internalState, identifier, type, getCurrSymTable());
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    /* Check whether identifier is declared as a VariableId, ParameterId or FunctionId
     * and return the type if so */
    Identifier id = symbolTable.lookupAll(identifier);

    if (id instanceof VariableId || id instanceof ParameterId || id instanceof ObjectId ) {
      return id.getType();
    }

    /* Add '*' to search for a FunctionId */
    id = symbolTable.lookupAll("*" + identifier);
    if (id instanceof FunctionId) {
      return id.getType();
    }

    /* Add 'attr*' to search for a FunctionId */
    id = symbolTable.lookupAll("attr*" + identifier);
    if (id instanceof VariableId) {
      return id.getType();
    }

    return null;
  }


  @Override
  public String toString() {
    return identifier;
  }
}