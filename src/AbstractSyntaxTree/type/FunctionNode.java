package AbstractSyntaxTree.type;

import static InternalRepresentation.Enums.Directive.LTORG;
import static InternalRepresentation.Enums.Register.*;

import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import InternalRepresentation.Instructions.DirectiveInstruction;
import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.PopInstruction;
import InternalRepresentation.Instructions.PushInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.FunctionId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class FunctionNode implements TypeNode {

  /* returnType:    TypeNode of the return type
   * identifier:    IdentifierNode of the function's declaration
   * params:        ParamListNode containing a list of the function's parameter
   *                  IdentifierNodes and TypeNodes
   * bodyStatement: Root node for the statement tree of the function's body */
  private final TypeNode returnType;
  private final IdentifierNode identifier;
  private final ParamListNode params;
  private final StatementNode bodyStatement;

  /* currSymTable: stores the SymbolTable corresponding to the function's scope.
   * Set before semanticAnalysis begins in ProgramNode */
  private SymbolTable currSymTable = null;

  public FunctionNode(TypeNode returnType, IdentifierNode identifier,
      ParamListNode params, StatementNode bodyStatement) {
    this.returnType = returnType;
    this.identifier = identifier;
    this.params = params;
    this.bodyStatement = bodyStatement;
  }

  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }

  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

  /* Returns the function identifier string as is stored in the symbol table.
   * Character '*' added to differentiate from variable and parameter names */
  public String getName() {
    return "*" + identifier.getIdentifier();
  }

  /* Returns identifier without extra '*' character */
  public IdentifierNode getIdentifierNode() {
    return identifier;
  }

  /* Check for whether the function has either an exit statement or return statement
   * or if there are statements after a return statement in the function body */
  public String checkSyntaxErrors() {
    StringBuilder errorMessage = new StringBuilder();
    if ((!bodyStatement.hasReturnStatement() && !bodyStatement.hasExitStatement())
        || (bodyStatement.hasReturnStatement() && !bodyStatement.hasNoStatementAfterReturn())) {
      errorMessage.append("Syntax error at line ").append(identifier.getLine()).append(":")
          .append(identifier.getCharPositionInLine()).append(" function ")
          .append(identifier.getIdentifier())
          .append(" does not end with a return or exit statement");
    }

    return errorMessage.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Set the expected return type in the corresponding ReturnStatementNode of the
     * function body */
    bodyStatement.setReturnType(returnType.getType());

    /* Recursively call semanticAnalysis on each stored node */
    params.semanticAnalysis(symbolTable, errorMessages);
    bodyStatement.semanticAnalysis(symbolTable, errorMessages);
    returnType.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.resetAvailableRegs();

    internalState.addInstruction(new LabelInstruction("f_" + identifier.getIdentifier()));
    internalState.addInstruction(new PushInstruction(LR));
    params.generateAssembly(internalState);
    bodyStatement.generateAssembly(internalState);
    internalState.addInstruction(new PopInstruction(PC));
    internalState.addInstruction(new DirectiveInstruction(LTORG));
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return new FunctionId(identifier, returnType.getType(),
        params.getIdentifiers(symbolTable));
  }

  @Override
  public DataTypeId getType() {
    return returnType.getType();
  }

  @Override
  public String toString() {
    return identifier.getIdentifier();
  }
}