package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.type.AttributeNode;
import AbstractSyntaxTree.type.TypeNode;
import InternalRepresentation.Instructions.*;
import InternalRepresentation.Instructions.StrInstruction.StrType;
import InternalRepresentation.InternalState;
import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Operand;
import InternalRepresentation.Utils.Register;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;
import java.util.stream.Collectors;

import static InternalRepresentation.Instructions.ArithmeticInstruction.ArithmeticOperation.ADD;
import static InternalRepresentation.Instructions.BranchInstruction.BranchOperation.B;
import static InternalRepresentation.Instructions.LdrInstruction.LdrType.LDR;
import static InternalRepresentation.Instructions.StrInstruction.StrType.STRB;
import static InternalRepresentation.Utils.BuiltInFunction.SystemBuiltIn.MALLOC;
import static InternalRepresentation.Utils.Register.DEST_REG;
import static InternalRepresentation.Utils.Register.SP;

public class ObjectDeclStatementNode extends StatementNode {

  private final IdentifierNode className;
  private final IdentifierNode objectName;
  private final List<ExpressionNode> expressions;

  private static final int BYTE_SIZE = 1;
  private static final int ADDRESS_BYTE_SIZE = 4;
  private static final int MAX_DEALLOCATE_SIZE = 1024;

  public ObjectDeclStatementNode(IdentifierNode className, IdentifierNode objectName,
      List<ExpressionNode> expressions) {
    this.className = className;
    this.objectName = objectName;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);
    /* Check if className is a valid class name */
    Identifier classId = symbolTable.lookupAll("class*" + className.getIdentifier());
    ObjectId newObject = null;

    if (!(classId instanceof ClassType)) {
      errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
              "Cannot declare object." + " Expected: CLASS TYPE "
              + " Actual: " + classId));
    } else {
      /* Try to find matching constructor for these parameter types */
      List<ConstructorId> constructors = ((ClassType) classId).getConstructors();

      /* Get types of all expressions */
      List<DataTypeId> exprTypes = expressions.stream().
              map(x -> x.getType(symbolTable)).
              collect(Collectors.toList());

      for (ConstructorId constructor : constructors) {
        List<DataTypeId> parameters = constructor.getParameterTypes();
        if (parameters.size() == exprTypes.size()) {
          int i;

          for (i = 0; i < parameters.size(); i++) {
            if(exprTypes.get(i) == null) {
              errorMessages.add(new SemanticError(expressions.get(i).getLine(),
                  expressions.get(i).getCharPositionInLine(),
                      "Could not resolve type of parameter "
                      + (i + 1)
                      + " in '"
                      + className.getIdentifier()
                      + "' function."
                      + " Expected: "
                      + parameters.get(i).getType()));
            } else if (!exprTypes.get(i).equals(parameters.get(i))) {
              break;
            }
          }

          if (i == parameters.size()) {
            newObject = new ObjectId(objectName, (ClassType) classId, constructor);
            break;
          }
        }
      }

      if (newObject == null) {
        errorMessages.add(new SemanticError(objectName.getLine(),
            objectName.getCharPositionInLine(),
                "Could not find matching constructor for object '"
                    + objectName.getIdentifier() + "'."));
      }
    }

    /* Check if objectName is being redeclared, if not make ObjectId */
    if (symbolTable.lookupAll(objectName.getIdentifier()) != null) {
      errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
              "Identifier '" + objectName.getIdentifier()
              + "' has already been declared in the same scope."));
    } else if (newObject != null){
      symbolTable.add(objectName.getIdentifier(), newObject);
    }

    for(ExpressionNode expr : expressions) {
      expr.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }

    objectName.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
  }

  // allocate space on the heap for this object
  // set value of each attribute to whatever - if passed to constructor get from here otherwise get from class decl
  // put that on the heap
  @Override
  public void generateAssembly(InternalState internalState) {

    ClassType classId = (ClassType) getCurrSymTable().lookupAll("class*" + className.getIdentifier());

    /* Set offset of the object in the current Symbol Table */
    getCurrSymTable().setVarsOffset(objectName.getIdentifier(), ADDRESS_BYTE_SIZE);

    /* Malloc space on the heap for all attributes */
    List<AttributeNode> attributes = classId.getAttributes();
    int numAttributes = attributes.size();
    internalState.addInstruction(
            new LdrInstruction(LdrInstruction.LdrType.LDR, DEST_REG, numAttributes * ADDRESS_BYTE_SIZE));

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
        StrInstruction.StrType strInstr = (size == BYTE_SIZE) ? STRB : StrInstruction.StrType.STR;
        internalState.addInstruction(new StrInstruction(strInstr, exprReg, DEST_REG));
      }

      /* Store the reference on the stack */
      internalState.addInstruction(
              new StrInstruction(StrInstruction.StrType.STR, DEST_REG, attributeReg, offset * ADDRESS_BYTE_SIZE));

      if (attribute.hasAssignRHS()) {
        internalState.pushFreeRegister(exprReg);
      }
      offset++;
    }


    /* Calculate total arguments size in argsTotalSize */
    int argsTotalSize = 0;

    /* Arguments are stored in decreasing order they are given in the code */
    for (int i = expressions.size() - 1; i >= 0; i--) {
      /* Get argument, calculate size and add it to argsTotalSize */
      ExpressionNode currArg = expressions.get(i);

      /* Generate assembly code for the current argument */
      currArg.generateAssembly(internalState);

      int argSize = currArg.getType(getCurrSymTable()).getSize();

      StrType strInstr = (argSize == 1) ? STRB : StrType.STR;

      /* Store currArg on the stack and decrease stack pointer (stack grows downwards) */
      internalState.addInstruction(new StrInstruction(strInstr, internalState.peekFreeRegister(),
          SP, -argSize, true));
      argsTotalSize += argSize;

      getCurrSymTable().incrementArgsOffset(argSize);

    }
    /* Store object reference on the stack and decrease stack pointer (stack grows downwards) */
    objectName.generateAssembly(internalState);
    internalState.addInstruction(new StrInstruction(StrInstruction.StrType.STR,
        internalState.peekFreeRegister(), SP, -ADDRESS_BYTE_SIZE, true));
    argsTotalSize += ADDRESS_BYTE_SIZE;

    getCurrSymTable().incrementArgsOffset(ADDRESS_BYTE_SIZE);

    getCurrSymTable().resetArgsOffset();

    /* Branch Instruction to the callee label */
    List<DataTypeId> argTypes = expressions.stream().map(e -> e.getType(getCurrSymTable()))
        .collect(Collectors.toList());

    /* Get index of constructor from classType */
    String index = Integer.toString(classId.findIndexConstructor(argTypes));

    String functionLabel = "class_constr_" + className.toString() + index;
    internalState.addInstruction(new BranchInstruction(ConditionCode.L, B, functionLabel));

//    /* De-allocate stack from the function arguments. Max size for one de-allocation is 1024B */
//    while (argsTotalSize > 0) {
//      internalState.addInstruction(
//          new ArithmeticInstruction(ADD, SP, SP,
//              new Operand(Math.min(argsTotalSize, MAX_DEALLOCATE_SIZE)), false));
//      argsTotalSize -= Math.min(argsTotalSize, MAX_DEALLOCATE_SIZE);
//    }
  }
}