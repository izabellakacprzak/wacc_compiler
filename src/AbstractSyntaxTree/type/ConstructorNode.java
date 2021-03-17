package AbstractSyntaxTree.type;

import static InternalRepresentation.Instructions.DirectiveInstruction.Directive.LTORG;
import static InternalRepresentation.Utils.Register.LR;
import static InternalRepresentation.Utils.Register.PC;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import InternalRepresentation.Instructions.DirectiveInstruction;
import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.PopInstruction;
import InternalRepresentation.Instructions.PushInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;

public class ConstructorNode implements TypeNode{

  private final IdentifierNode name;
  private final ParamListNode parameters;
  private final StatementNode bodyStatement;

  private SymbolTable currSymTable = null;

  public ConstructorNode(IdentifierNode name, ParamListNode parameters,
      StatementNode bodyStatement) {
    this.name = name;
    this.parameters = parameters;
    this.bodyStatement = bodyStatement;
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return null;
  }

  @Override
  public DataTypeId getType() {
    return null;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);

    /* Get class name and search it up in symbol table */
    String className = "class_" + name.getIdentifier();
    Identifier classId = symbolTable.lookup(className);

    /* Check if name matches class name */
    if (classId == null) {
      errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
              "Constructor name '" + name.getIdentifier()
                  + "' does not match declared class."));
    } else if (!(classId instanceof ClassType)) {
      errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
              "Constructor '" + name.getIdentifier()
                  + "' can only be declared for a class."));
    } else {
      ClassType classIdentifier = (ClassType) classId;
      ConstructorId constructor = new ConstructorId(name, parameters.getIdentifiers(symbolTable));

      /* Check if such constructor already exists, if not add to list of constructors in symbol table */
      if (!classIdentifier.addConstructor(constructor)){
        errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
                "Constructor '" + constructor.toString() + "' has already been declared."));
      }
    }

    parameters.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    bodyStatement.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    // do what we do for function
    /* Reset registers to start generating a function */
    internalState.resetAvailableRegs();

    /* Add function label and push Link Register */
    ClassType classId = (ClassType) currSymTable.lookup("class_" + name.getIdentifier());

    /* Get index of constructor from classType */
    String index = Integer.toString(classId.findIndex(parameters.getIdentifiers(currSymTable)));

    internalState.addInstruction(new LabelInstruction("class_constr_" + name.getIdentifier() + index));
    internalState.addInstruction(new PushInstruction(LR));

    /* Allocate space for variables in the function's currSymbolTable */
    internalState.allocateStackSpace(currSymTable);

    /* Visit and generate assembly for the function's ParamListNode */
    parameters.generateAssembly(internalState);

    /* Visit and generate assembly for the function's StatementNode */
    bodyStatement.generateAssembly(internalState);

    /* Reset the parameters' offset, pop the PC program counter add the
     *   .ltorg instruction to finish the function */
    internalState.resetParamStackOffset();
    internalState.addInstruction(new PopInstruction(PC));
    internalState.addInstruction(new DirectiveInstruction(LTORG));
  }

  @Override
  public void setCurrSymTable(SymbolTable currSymTable) {
    this.currSymTable = currSymTable;
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}
