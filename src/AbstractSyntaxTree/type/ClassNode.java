package AbstractSyntaxTree.type;

import static InternalRepresentation.Instructions.BranchInstruction.BranchOperation.B;
import static InternalRepresentation.Instructions.DirectiveInstruction.Directive.LTORG;
import static InternalRepresentation.Instructions.LdrInstruction.LdrType.LDR;
import static InternalRepresentation.Instructions.StrInstruction.StrType.STRB;
import static InternalRepresentation.Utils.BuiltInFunction.SystemBuiltIn.MALLOC;
import static InternalRepresentation.Utils.Register.DEST_REG;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.DirectiveInstruction;
import InternalRepresentation.Instructions.LabelInstruction;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.Instructions.LdrInstruction.LdrType;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.Instructions.StrInstruction;
import InternalRepresentation.Instructions.StrInstruction.StrType;
import InternalRepresentation.InternalState;
import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Register;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;

public class ClassNode implements TypeNode {

  private static final int BYTE_SIZE = 1;
  private static final int ADDRESS_BYTE_SIZE = 4;
  private final IdentifierNode className;
  private final List<AttributeNode> attributes;
  private final List<ConstructorNode> constructors;
  private final List<FunctionNode> methods;
  private SymbolTable currSymTable = null;

  public ClassNode(IdentifierNode className, List<AttributeNode> attributes,
      List<ConstructorNode> constructors, List<FunctionNode> methods) {
    this.className = className;
    this.attributes = attributes;
    this.constructors = constructors;
    this.methods = methods;
  }

  public String getName() {
    return "class*" + className.getIdentifier();
  }

  /* Returns identifier without extra 'class*' characters */
  public IdentifierNode getIdentifierNode() {
    return className;
  }

  @Override
  public Identifier getIdentifier(SymbolTable symbolTable) {
    return new ClassType(className, attributes);
  }

  @Override
  public DataTypeId getType() {
    return null;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);

    for (AttributeNode attribute : attributes) {
      attribute.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }

    for (ConstructorNode constructor : constructors) {
      constructor.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }

    ProgramNode.overloadFunc(symbolTable, errorMessages, methods, uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    // put the class somewhere (heap)?
    // for each constructor and method do what we do for FunctionNode

    internalState.addInstruction(new LabelInstruction("class_" + className.getIdentifier()));

    /* Malloc space on the heap for all attributes */
    int numAttributes = attributes.size();
    internalState.addInstruction(
        new LdrInstruction(LdrType.LDR, DEST_REG, numAttributes * ADDRESS_BYTE_SIZE));

    internalState.addInstruction(new BranchInstruction(ConditionCode.L, B, MALLOC.getLabel()));

    Register attributeReg = internalState.popFreeRegister();
    internalState.addInstruction(new MovInstruction(attributeReg, DEST_REG));

    int offset = 0;

    for (AttributeNode attribute : attributes) {
      attribute.generateAssembly(internalState);
      Register exprReg = null;
      if (attribute.hasAssignRHS()) {
        exprReg = internalState.popFreeRegister();
      }
      /* Load expr type size into DEST_REG */
      int size = attribute.getType().getSize();
      internalState.addInstruction(new LdrInstruction(LDR, DEST_REG, size));

      /* Allocate space on the heap */
      internalState.addInstruction(new BranchInstruction(ConditionCode.L, B, MALLOC.getLabel()));

      if (attribute.hasAssignRHS()) {
        /* If the expr type size is 1 byte, use STRB, otherwise use STR */
        StrType strInstr = (size == BYTE_SIZE) ? STRB : StrType.STR;
        internalState.addInstruction(new StrInstruction(strInstr, exprReg, DEST_REG));
      }

      /* Store the reference on the stack */
      internalState.addInstruction(
          new StrInstruction(StrType.STR, DEST_REG, attributeReg, offset * ADDRESS_BYTE_SIZE));

      internalState.pushFreeRegister(exprReg);
      offset++;

    }

    internalState.addInstruction(new DirectiveInstruction(LTORG));

    /* generate assembly for constructors */
    for(ConstructorNode constructor : constructors) {
      constructor.generateAssembly(internalState);
    }

    /* generate assembly for methods */
    for(FunctionNode method : methods) {
      method.generateAssembly(internalState);
    }
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
