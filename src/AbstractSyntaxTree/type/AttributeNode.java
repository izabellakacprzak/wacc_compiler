package AbstractSyntaxTree.type;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;

import java.util.List;

public class AttributeNode implements TypeNode {

  /* name:          name of the attribute
   * type:          type of the attribute
   * assignRHS:     optional default value assigned to the attribute
   * currSymTable:  current symbol table */
  private final IdentifierNode name;
  private final TypeNode type;
  private final AssignRHSNode assignRHS;
  private SymbolTable currSymTable = null;


  /* ATTRIBUTE WITH DEFAULT RHS */
  public AttributeNode(IdentifierNode name, TypeNode type, AssignRHSNode assignRHS) {
    this.name = name;
    this.type = type;
    this.assignRHS = assignRHS;
  }

  /* ATTRIBUTE WITHOUT DEFAULT RHS */
  public AttributeNode(IdentifierNode name, TypeNode type) {
    this.name = name;
    this.type = type;
    this.assignRHS = null;
  }

  public boolean hasAssignRHS() {
    return assignRHS != null;
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return null;
  }

  @Override
  public DataTypeId getType() {
    return type.getType();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);

    /* Check if such an attribute has not been defined */
    if (symbolTable.lookup(name.getIdentifier()) != null) {
      errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
              "Field '" + name.getIdentifier() + "' has already been declared."));
    }

    /* If not, check if there is an assign RHS and if so do type checks */
    if (assignRHS != null) {
      assignRHS.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
      DataTypeId assignedType = assignRHS.getType(symbolTable);
      if (assignedType == null) {
        errorMessages.add(new SemanticError(assignRHS.getLine(), assignRHS.getCharPositionInLine(),
                "RHS type could not be resolved."));
      } else if (type.getType() == null) {
        errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
                "Field type could not be resolved."));
      } else if (!assignedType.equals(type.getType())) {
        errorMessages.add(new SemanticError(name.getLine(), name.getCharPositionInLine(),
                "Assignment type does not match declared type for '"
                + name.getIdentifier() + "'."
                + " Expected: " + type.getType() + " Actual: " + assignedType));
        symbolTable.add("attr*" + name.getIdentifier(), new VariableId(name, type.getType()));
      } else {
        symbolTable.add("attr*" + name.getIdentifier(), new VariableId(name, type.getType()));
      }
    } else {
      /* Otherwise create identifier and put it in the symbol table */
      symbolTable.add("attr*" + name.getIdentifier(), new VariableId(name, type.getType()));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitAttributeNode(internalState, assignRHS);
  }

  @Override
  public String toString() {
    return name.getIdentifier();
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
