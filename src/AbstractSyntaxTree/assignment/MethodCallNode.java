package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import InternalRepresentation.Instructions.ArithmeticInstruction;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.Instructions.StrInstruction;
import InternalRepresentation.InternalState;
import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Operand;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ClassType;

import java.util.List;
import java.util.stream.Collectors;

import static InternalRepresentation.Instructions.ArithmeticInstruction.ArithmeticOperation.ADD;
import static InternalRepresentation.Instructions.BranchInstruction.BranchOperation.B;
import static InternalRepresentation.Instructions.StrInstruction.StrType.STRB;
import static InternalRepresentation.Utils.Register.DEST_REG;
import static InternalRepresentation.Utils.Register.SP;

public class MethodCallNode extends CallNode{

  private final IdentifierNode objectName;
  private final IdentifierNode methodName;
  private final List<ExpressionNode> arguments;
  private DataTypeId returnType = null;

  private static final int MAX_DEALLOCATE_SIZE = 1024;
  private static final int ADDRESS_BYTE_SIZE = 4;

  public MethodCallNode(int line, int charPositionInLine, IdentifierNode objectName,
      IdentifierNode methodName, List<ExpressionNode> arguments) {
    super(line, charPositionInLine);
    this.objectName = objectName;
    this.methodName = methodName;
    this.arguments = arguments;
  }

  public Identifier getIdentifier(SymbolTable symbolTable) {
    return symbolTable.lookup("*" + methodName.getIdentifier());
  }

  public DataTypeId getReturnType() {
    return returnType;
  }

  public void setReturnType(DataTypeId type) {
    returnType = type;
  }

  public SymbolTable getClassSymTable(SymbolTable symbolTable) {
    Identifier object = symbolTable.lookupAll(objectName.getIdentifier());
    if (!(object instanceof ObjectId)) {
      return null;
    }

    ObjectId objectId = (ObjectId) object;
    DataTypeId classType = objectId.getType();
    if (!(classType instanceof ClassType)) {
      return null;
    }

    return  ((ClassType) classType).getAttributes().get(0).getCurrSymTable();
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    SymbolTable classTable = getClassSymTable(symbolTable);

    if(classTable == null) {
      return null;
    }

    Identifier functionId = classTable.lookupAll("*" + methodName.getIdentifier());
    FunctionId function = (FunctionId) functionId;

    if (function == null) {
      return null;
    }

    return function.getType();
  }

  public List<DataTypeId> getOverloadType(SymbolTable symbolTable) {
    SymbolTable classTable = getClassSymTable(symbolTable);

    if(classTable == null) {
      return null;
    }

    Identifier functionId = classTable.lookupAll("*" + methodName.getIdentifier());
    return getOverloadDataTypeIds(symbolTable, (OverloadFuncId) functionId, arguments);
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("method: ").append(methodName.getIdentifier()).append('(');

    return getString(str, arguments);
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    setCurrSymTable(symbolTable);

    /* Check if object has been declared and is in fact an object */
    objectName.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    Identifier object = symbolTable.lookupAll(objectName.getIdentifier());
    if (!(object instanceof ObjectId)) {
      errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
              "Cannot call a method on a non-object."   + " Expected: OBJECT TYPE "
              + " Actual: " + object));
    } else {
      ObjectId objectId = (ObjectId) object;
      DataTypeId classType = objectId.getType();
      if (!(classType instanceof ClassType)) {
        errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
                "Could not properly resolve object type."   + " Expected: CLASS TYPE "
                + " Actual: " + classType));
      } else {
        SymbolTable classTable = ((ClassType) classType).getAttributes().get(0).getCurrSymTable();
        Identifier functionId = classTable.lookup("*" + methodName.getIdentifier());

        /* Check if method has been declared in the appropriate class */
        if (functionId == null) {
          errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
                  "Could not find method with signature '"  + methodName.getIdentifier() + "' declared in class "
                  + ((ClassType) classType).getClassName()));
        } else {
          super.semAnalyseFunctionArgs(symbolTable, errorMessages, methodName, arguments,
              functionId, uncheckedNodes, firstCheck);
        }
      }
    }
    /* Semantically analyse all arguments */
    for (ExpressionNode arg : arguments) {
      arg.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {

    SymbolTable currSymTable = getCurrSymTable();
    Identifier object = currSymTable.lookupAll(objectName.getIdentifier());
    String className = ((ClassType) object.getType()).getClassName();
    if (!className.equals("")) {
      className = "class_" + className + "_";
    }

    /* Calculate total arguments size in argsTotalSize */
    int argsTotalSize = 0;

    /* Arguments are stored in decreasing order they are given in the code */
    for (int i = arguments.size() - 1; i >= 0; i--) {
      /* Get argument, calculate size and add it to argsTotalSize */
      ExpressionNode currArg = arguments.get(i);

      /* Generate assembly code for the current argument */
      currArg.generateAssembly(internalState);

      int argSize = currArg.getType(currSymTable).getSize();

      StrInstruction.StrType strInstr = (argSize == 1) ? STRB : StrInstruction.StrType.STR;

      /* Store currArg on the stack and decrease stack pointer (stack grows downwards) */
      internalState.addInstruction(new StrInstruction(strInstr, internalState.peekFreeRegister(),
              SP, -argSize, true));
      argsTotalSize += argSize;

      currSymTable.incrementArgsOffset(argSize);

    }

    /* Store object reference on the stack and decrease stack pointer (stack grows downwards) */
    objectName.generateAssembly(internalState);
    internalState.addInstruction(new StrInstruction(StrInstruction.StrType.STR,
        internalState.peekFreeRegister(), SP, -ADDRESS_BYTE_SIZE, true));
    argsTotalSize += ADDRESS_BYTE_SIZE;

    currSymTable.incrementArgsOffset(ADDRESS_BYTE_SIZE);

    currSymTable.resetArgsOffset();

    /* Branch Instruction to the callee label */
    String index = "";
    List<DataTypeId> argTypes = arguments.stream().map(e -> e.getType(currSymTable))
            .collect(Collectors.toList());
    Identifier functionIdentifier = currSymTable.lookupAll("*" + methodName.getIdentifier());
    if(functionIdentifier instanceof OverloadFuncId) {
      OverloadFuncId overloadFuncId = (OverloadFuncId) functionIdentifier;
      FunctionId functionId = overloadFuncId.findFuncReturnType(argTypes, returnType);
      index = String.valueOf(overloadFuncId.getIndex(functionId));
    }

    String functionLabel = "f_" + className + methodName.toString() + index;
    internalState.addInstruction(new BranchInstruction(ConditionCode.L, B, functionLabel));

    /* De-allocate stack from the function arguments. Max size for one de-allocation is 1024B */
    while (argsTotalSize > 0) {
      internalState.addInstruction(
              new ArithmeticInstruction(ADD, SP, SP,
                      new Operand(Math.min(argsTotalSize, MAX_DEALLOCATE_SIZE)), false));
      argsTotalSize -= Math.min(argsTotalSize, MAX_DEALLOCATE_SIZE);
    }

    /* Move the result stored in DEST_REG in the first free register */
    internalState
            .addInstruction(new MovInstruction(internalState.peekFreeRegister(), DEST_REG));


  }
}