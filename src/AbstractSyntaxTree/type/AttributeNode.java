package AbstractSyntaxTree.type;

import static InternalRepresentation.Instructions.StrInstruction.StrType.STRB;
import static InternalRepresentation.Utils.Register.SP;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Instructions.StrInstruction;
import InternalRepresentation.Instructions.StrInstruction.StrType;
import InternalRepresentation.InternalState;
import InternalRepresentation.Utils.Register;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.VariableId;

import java.util.List;

public class AttributeNode implements TypeNode {

  private final IdentifierNode name;
  private final TypeNode type;
  private final AssignRHSNode assignRHS;
  private SymbolTable currSymTable = null;


  public AttributeNode(IdentifierNode name, TypeNode type, AssignRHSNode assignRHS) {
    this.name = name;
    this.type = type;
    this.assignRHS = assignRHS;
  }

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
        symbolTable.add(name.getIdentifier(), new VariableId(name, type.getType()));
      } else {
        symbolTable.add(name.getIdentifier(), new VariableId(name, type.getType()));
      }
    } else {
      /* Otherwise create identifier and put it in the symbol table */
      symbolTable.add(name.getIdentifier(), new VariableId(name, type.getType()));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    if(assignRHS != null) {
      assignRHS.generateAssembly(internalState);
    }
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
