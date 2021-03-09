package InternalRepresentation;

import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.assignment.PairElemNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import AbstractSyntaxTree.type.ParamListNode;
import AbstractSyntaxTree.type.TypeNode;
import InternalRepresentation.Instructions.*;
import InternalRepresentation.Instructions.LdrInstruction.LdrType;
import InternalRepresentation.Instructions.StrInstruction.StrType;
import InternalRepresentation.Utils.ConditionCode;
import InternalRepresentation.Utils.Operand;
import InternalRepresentation.Utils.Register;
import InternalRepresentation.Utils.Shift;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.Operator;
import SemanticAnalysis.SymbolTable;

import java.util.List;

import static InternalRepresentation.Instructions.ArithmeticInstruction.ArithmeticOperation.*;
import static InternalRepresentation.Instructions.LogicalInstruction.LogicalOperation.*;
import static InternalRepresentation.Instructions.StrInstruction.StrType.*;
import static InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn.*;
import static InternalRepresentation.Utils.ConditionCode.*;
import static InternalRepresentation.Utils.BuiltInFunction.SystemBuiltIn.*;
import static InternalRepresentation.Instructions.DirectiveInstruction.Directive.*;
import static InternalRepresentation.Instructions.BranchInstruction.BranchOperation.*;
import static InternalRepresentation.Instructions.LdrInstruction.LdrType.*;
import static InternalRepresentation.Utils.Register.*;
import static InternalRepresentation.Utils.Shift.ShiftType.*;

public class CodeGenVisitor {

  /* Representation of true and false as ints */
  private static final int TRUE = 1;
  private static final int FALSE = 0;

  /* Representation of null as an int */
  private static final int NULL = 0;

  /* Constants for pair use */
  private static final int FST = 0;
  private static final int SND = 1;
  private static final int NUM_PAIR_ELEMS = 2;

  /* Constant sizes */
  private static final int BYTE_SIZE = 1;
  private static final int INT_BYTES_SIZE = 4;
  private static final int ADDRESS_BYTE_SIZE = 4;
  private static final int MAX_DEALLOCATE_SIZE = 1024;

  /* Exit code for a successful execution */
  private static final int EXIT_SUCCESS = 0;

  /* For use as an immediate offset */
  private static final int ZERO_OFFSET = 0;

  /* Shift right value for MUL operation */
  private static final int MUL_SHIFT = 31;

  /* For use when setting condition flag bits in arithmetic instructions */
  private static final boolean SET_BITS = true;

  /* Each visitor function visits a type of ASTNode from the AbstractSyntaxTree.
   * Visitor functions must all take an InternalState to add generated instructions to.
   * Other parameters refer to values stored in the visited ASTNode */

  public void visitProgramNode(InternalState internalState,
                               StatementNode statementNode, List<FunctionNode> functionNodes) {

    /* Visit and generate assembly for each FunctionNode */
    for (FunctionNode functionNode : functionNodes) {
      functionNode.generateAssembly(internalState);
    }

    /* Add main label and push Link Register */
    internalState.addInstruction(new LabelInstruction("main"));
    internalState.addInstruction(new PushInstruction(LR));

    /* Allocate space for variables in the program StatementNode's currSymbolTable */
    internalState.allocateStackSpace(statementNode.getCurrSymTable());

    /* Visit and generate assembly for the program StatementNode */
    statementNode.generateAssembly(internalState);

    /* Allocate space for variables in the program StatementNode's currSymbolTable */
    internalState.deallocateStackSpace(statementNode.getCurrSymTable());

    /* Load success code to the DEST_REG, pop the PC program counter and add the
     *   .ltorg instruction to finish the program */
    internalState.addInstruction(new LdrInstruction(LDR, DEST_REG, EXIT_SUCCESS));
    internalState.addInstruction(new PopInstruction(PC));
    internalState.addInstruction(new DirectiveInstruction(LTORG));
  }

  public void visitFunctionNode(InternalState internalState, IdentifierNode identifier,
                                ParamListNode params, StatementNode bodyStatement, SymbolTable currSymTable) {

    /* Reset registers to start generating a function */
    internalState.resetAvailableRegs();

    /* Add function label and push Link Register */
    internalState.addInstruction(new LabelInstruction("f_" + identifier.getIdentifier()));
    internalState.addInstruction(new PushInstruction(LR));

    /* Allocate space for variables in the function's currSymbolTable */
    internalState.allocateStackSpace(currSymTable);

    /* Visit and generate assembly for the function's ParamListNode */
    params.generateAssembly(internalState);

    /* Allocate space for variables in the function's currSymbolTable */
    internalState.setFunctionSymTable(currSymTable);

    /* Visit and generate assembly for the function's StatementNode */
    bodyStatement.generateAssembly(internalState);

    /* Reset the parameters' offset, pop the PC program counter add the
     *   .ltorg instruction to finish the function */
    internalState.resetParamStackOffset();
    internalState.addInstruction(new PopInstruction(PC));
    internalState.addInstruction(new DirectiveInstruction(LTORG));
  }

  public void visitParamListNode(InternalState internalState,
                                 List<IdentifierNode> identifiers, SymbolTable currSymTable) {

    /* Initially increment the parameters' stack offset by the size of an address */
    //internalState.incrementParamStackOffset(ADDRESS_BYTE_SIZE);

    /* Set the offset of each identifier in the currSymbolTable (for the function scope) */
    currSymTable.incrementDeclaredParamsOffset(ADDRESS_BYTE_SIZE);
    int typeSize;
    for (IdentifierNode identifier : identifiers) {
      typeSize = identifier.getType(currSymTable).getSize();
      currSymTable.setParamsOffset(identifier.getIdentifier(), typeSize);

      /* Increment the parameters' stack pointer by the size of each parameter */
      //int paramSize = identifier.getType(currSymTable).getSize();
      //internalState.incrementParamStackOffset(paramSize);
    }

  }

  public void visitAssignVarNode(InternalState internalState, AssignLHSNode left,
                                 AssignRHSNode right, SymbolTable currSymTable) {

    /* Visit and generate assembly for the right assignment and get result from register stack */
    right.generateAssembly(internalState);
    Register rightNodeResult = internalState.popFreeRegister();

    /* Switch based on the instance of AssignLHSNode */
    if (left instanceof IdentifierNode) {
      /* Get the size and offset of the IdentifierNode being reassigned */
      int typeSize = left.getType(currSymTable).getSize();
      int offset = currSymTable.getOffset(((IdentifierNode) left).getIdentifier());

      /* Find the type of store instruction based on the size and store
       *   rightNodeResult on the stack in the correct position */
      StrType strType = typeSize == BYTE_SIZE ? STRB : STR;
      internalState.addInstruction(new StrInstruction(strType, rightNodeResult, SP, offset));

    } else if (left instanceof PairElemNode) {
      /* Cast left to PairElemNode, take a free register (to store the pair pointer)
       * from the register stack and take the offset of the pairElem from the current SymbolTable */
      PairElemNode pairElem = (PairElemNode) left;
      Register pairPointer = internalState.peekFreeRegister();
      int offset = currSymTable.getOffset(pairElem.getIdentifier());

      /* Load the pair pointer from the stack and move it to the DEST_REG before branching
       * to the p_check_null_pointer CustomBuiltIn function */
      internalState.addInstruction(new LdrInstruction(LDR, pairPointer, SP, offset));
      internalState.addInstruction(new MovInstruction(DEST_REG, pairPointer));
      internalState.addInstruction(new BranchInstruction(BL, NULL_POINTER));

      /* Load the pair element to the pairPointer register */
      internalState.addInstruction(
          new LdrInstruction(LDR, pairPointer, pairPointer,
              pairElem.getPosition() * ADDRESS_BYTE_SIZE));

      /* Find the type of store instruction based on the size and store
       *   rightNodeResult on the stack in the correct position */
      StrType strType = pairElem.getType(currSymTable).getSize() == BYTE_SIZE ? STRB : StrType.STR;
      internalState.addInstruction(new StrInstruction(strType, rightNodeResult, pairPointer));

    } else if (left instanceof ArrayElemNode) {
      /* Cast left to ArrayElemNode, take a free register (to store the array elem pointer)
       * from the register stack */
      ArrayElemNode arrayElem = (ArrayElemNode) left;
      Register arrayReg = internalState.popFreeRegister();

      /* Generate instructions for loading the array element */
      generateElemAddr(internalState, arrayReg, arrayElem);

      /* Find the type of store instruction based on the size and store
       *   rightNodeResult on the stack in the correct position */
      StrType strType = arrayElem.getType(currSymTable).getSize() == BYTE_SIZE ? STRB : StrType.STR;
      internalState.addInstruction(new StrInstruction(strType, rightNodeResult, arrayReg));

      /* Push arrayReg back to the register stack */
      internalState.pushFreeRegister(arrayReg);
    }

    /* Push rightNodeResult back to the register stack */
    internalState.pushFreeRegister(rightNodeResult);
  }

  private void generateElemAddr(InternalState internalState, Register arrayReg,
                                ArrayElemNode arrayElem) {
    /* Take the identifier and currSymbolTable from the ArrayElemNode */
    IdentifierNode identifier = arrayElem.getIdentifier();
    SymbolTable currSymTable = arrayElem.getCurrSymTable();

    /* Take stack offset of the array from the SymbolTable and put address of array into arrayReg */
    int offset = currSymTable.getOffset(identifier.getIdentifier());
    internalState.addInstruction(
        new ArithmeticInstruction(ADD, arrayReg, SP, new Operand(offset), !SET_BITS));

    /* Evaluate each index expression */
    for (ExpressionNode expression : arrayElem.getExpressions()) {
      /* Visit and generate assembly for the index ExpressionNode */
      expression.generateAssembly(internalState);

      /* Take a free register from the register stack (to store the evaluated index)
       * and load array pointer into it */
      Register exprReg = internalState.peekFreeRegister();
      internalState.addInstruction(new LdrInstruction(LDR, arrayReg, arrayReg));

      /* Move the index into the DEST_REG and array pointer to ARG_REG_1 in preparation to
       * branch to the p_check_array_bounds function */
      internalState.addInstruction(new MovInstruction(DEST_REG, exprReg));
      internalState.addInstruction(new MovInstruction(ARG_REG_1, arrayReg));
      internalState.addInstruction(new BranchInstruction(BL, ARRAY_BOUNDS));

      /* Find position of element before adding size in array according to index */
      internalState.addInstruction(new ArithmeticInstruction(
          ADD, arrayReg, arrayReg, new Operand(INT_BYTES_SIZE), !SET_BITS));

      /* Add size to find position of element on the stack */
      DataTypeId arrayElemType = ((ArrayType) identifier.getType(currSymTable)).getElemType();
      if (arrayElemType instanceof BaseType
              && ((BaseType) arrayElemType).getBaseType() == BaseType.Type.CHAR) {
        internalState.addInstruction(new ArithmeticInstruction(ADD, arrayReg, arrayReg,
            new Operand(exprReg), !SET_BITS));

      } else {
        internalState.addInstruction(new ArithmeticInstruction(ADD, arrayReg, arrayReg,
            new Operand(exprReg, new Shift(LSL, INT_BYTES_SIZE / 2)), !SET_BITS));
      }
    }
  }

  public void visitDeclarationStatementNode(InternalState internalState, AssignRHSNode assignment,
                                            TypeNode type, IdentifierNode identifier, SymbolTable currSymTable) {
    /* Visit and generate assembly for the function's AssignRHSNode and store the result
     *   in destReg */
    assignment.generateAssembly(internalState);
    Register destReg = internalState.peekFreeRegister();

    /* Set the offset of the declaration identifier based on the size of the AssignRHSNode */
    int typeSize = type.getType().getSize();
    //internalState.decrementArgStackOffset(typeSize);
    currSymTable.setVarsOffset(identifier.getIdentifier(), typeSize);

    /* Find the correct store instruction type based on the size and store destReg in the
     * correct position on the stack */
    StrType storeType = typeSize == BYTE_SIZE ? STRB : StrType.STR;
    internalState.addInstruction(new StrInstruction(
        storeType, destReg, SP, currSymTable.getOffset(identifier.getIdentifier())));
  }

  public void visitExitStatementNode(InternalState internalState, ExpressionNode expression) {
    /* Visit and generate assembly for the exit code, then take the result register
     *   from the register stack */
    expression.generateAssembly(internalState);
    Register exitCodeReg = internalState.peekFreeRegister();

    /* Move the exit code to the DEST_REG before branching to exit */
    internalState.addInstruction(new MovInstruction(DEST_REG, exitCodeReg));
    internalState.addInstruction(new BranchInstruction(BL, "exit"));
  }

  public void visitFreeStatementNode(InternalState internalState, ExpressionNode expression) {
    /* Visit and generate assembly for the ExpressionNode */
    expression.generateAssembly(internalState);

    /* Move the result of the expression to DEST_REG before branching to the p_free_pair
     *   SystemBuiltIn function */
    internalState.addInstruction(new MovInstruction(DEST_REG, internalState.peekFreeRegister()));
    internalState.addInstruction(new BranchInstruction(BL, FREE_PAIR));
  }

  public void visitIfStatementNode(InternalState internalState, ExpressionNode condition,
                                   StatementNode thenStatement, StatementNode elseStatement) {
    /* Visit and generate assembly for the condition expression */
    condition.generateAssembly(internalState);

    /* Generate labels for else and endIf */
    String elseLabel = internalState.generateNewLabel();
    String endIfLabel = internalState.generateNewLabel();

    /* Branch to the elseLabel if the condition is FALSE */
    internalState.addInstruction(
        new CompareInstruction(internalState.peekFreeRegister(), new Operand(FALSE)));
    internalState.addInstruction(new BranchInstruction(EQ, B, elseLabel));

    /* Allocate stack space for the new thenStatement scope */
    internalState.allocateStackSpace(thenStatement.getCurrSymTable());

    /* Visit and generate assembly for the function's ParamListNode */
    thenStatement.generateAssembly(internalState);

    /* Dellocate stack space for the thenStatement scope */
    internalState.deallocateStackSpace(thenStatement.getCurrSymTable());

    /* Generate elseStatement with condition instructions */
    generateCondInstruction(internalState, elseStatement, endIfLabel, elseLabel);
  }

  public void visitWhileStatementNode(InternalState internalState, ExpressionNode condition,
                                      StatementNode statement) {
    /* Generate labels for cond and statement */
    String condLabel = internalState.generateNewLabel();
    String statementLabel = internalState.generateNewLabel();

    /* Generate statement with condition instructions */
    generateCondInstruction(internalState, statement, condLabel, statementLabel);

    /* Visit and generate assembly for the condition expression */
    condition.generateAssembly(internalState);

    /* Branch to the statement if the condition is true */
    internalState.addInstruction(
        new CompareInstruction(internalState.peekFreeRegister(), new Operand(TRUE)));
    internalState.addInstruction(new BranchInstruction(EQ, B, statementLabel));
  }

  private void generateCondInstruction(InternalState internalState, StatementNode statement,
                                       String condLabel, String statementLabel) {
    /* Add branch to condition label */
    internalState.addInstruction(new BranchInstruction(B, condLabel));

    /* Add label for the beginning of statement */
    internalState.addInstruction(new LabelInstruction(statementLabel));

    /* Allocate stack space for the new statement scope */
    internalState.allocateStackSpace(statement.getCurrSymTable());

    /* Visit and generate assembly for the function's ParamListNode */
    statement.generateAssembly(internalState);

    /* Deallocate stack space for the statement scope */
    internalState.deallocateStackSpace(statement.getCurrSymTable());

    /* Add label for the beginning of the condition */
    internalState.addInstruction(new LabelInstruction(condLabel));
  }

  public void visitNewScopeStatementNode(InternalState internalState, StatementNode statement) {
    /* Allocate stack space for the new statement scope */
    internalState.allocateStackSpace(statement.getCurrSymTable());

    /* Visit and generate assembly for the internal statement */
    statement.generateAssembly(internalState);

    /* Deallocate stack space for the statement scope */
    internalState.deallocateStackSpace(statement.getCurrSymTable());
  }

  public void visitPrintLineStatementNode(InternalState internalState, ExpressionNode expression,
                                          SymbolTable currSymTable) {
    /* Visit and generate assembly for a print node */
    visitPrintStatementNode(internalState, expression, currSymTable);

    /* Branch to the CustomBuiltIn p_print_ln function */
    internalState.addInstruction(new BranchInstruction(BL, PRINT_LN));
  }

  public void visitPrintStatementNode(InternalState internalState, ExpressionNode expression,
                                      SymbolTable currSymTable) {
    /* Visit and generate assembly for the expression to be printed and store in a new register */
    expression.generateAssembly(internalState);
    Register exprReg = internalState.peekFreeRegister();

    /* Move the expression to the DEST_REG for printing with a BuiltInFunction */
    internalState.addInstruction(new MovInstruction(DEST_REG, exprReg));

    /* Switch based on the type of the ExpressionNode */
    DataTypeId type = expression.getType(currSymTable);
    if (type instanceof ArrayType) {
      /* Print as a string if the array element type is CHAR, print as reference otherwise */
      if (((ArrayType) type).getElemType().equals(new BaseType(BaseType.Type.CHAR))) {
        internalState.addInstruction(new BranchInstruction(BL, PRINT_STRING));

      } else {
        internalState.addInstruction(new BranchInstruction(BL, PRINT_REFERENCE));
      }

    } else if (type instanceof PairType) {
      /* Print as a reference */
      internalState.addInstruction(new BranchInstruction(BL, PRINT_REFERENCE));

    } else if (type instanceof BaseType) {
      /* Print as base type */
      BaseType.Type baseType = ((BaseType) type).getBaseType();
      switch (baseType) {
        case CHAR:
          internalState.addInstruction(new BranchInstruction(BL, PUTCHAR.getLabel()));
          break;
        case STRING:
          internalState.addInstruction(new BranchInstruction(BL, PRINT_STRING));
          break;
        case BOOL:
          internalState.addInstruction(new BranchInstruction(BL, PRINT_BOOL));
          break;
        case INT:
          internalState.addInstruction(new BranchInstruction(BL, PRINT_INT));
          break;
      }
    }
  }

  public void visitReadStatementNode(InternalState internalState, AssignLHSNode assignment,
                                     SymbolTable currSymTable) {
    Register nextAvailable = internalState.peekFreeRegister();

    if (assignment instanceof IdentifierNode) {
      String identifier = ((IdentifierNode) assignment).getIdentifier();

      int offset = currSymTable.getOffset(identifier);
      internalState.addInstruction(new ArithmeticInstruction(ADD, nextAvailable, SP,
          new Operand(offset), !SET_BITS));
    } else {
      assignment.generateAssembly(internalState);
      internalState.addInstruction(new ArithmeticInstruction(ADD, nextAvailable, SP,
          new Operand(ZERO_OFFSET), !SET_BITS));
    }

    internalState.addInstruction(new MovInstruction(DEST_REG, nextAvailable));
    DataTypeId type = assignment.getType(currSymTable);

    if (type instanceof BaseType) {
      BaseType.Type baseType = ((BaseType) type).getBaseType();

      switch (baseType) {
        case INT:
          internalState.addInstruction(new BranchInstruction(BL, READ_INT));
          break;
        case CHAR:
          internalState.addInstruction(new BranchInstruction(BL, READ_CHAR));
          break;
      }
    }
  }

  public void visitReturnStatementNode(InternalState internalState, ExpressionNode returnExpr) {
    Register returnStatReg = internalState.peekFreeRegister();
    returnExpr.generateAssembly(internalState);

    internalState.addInstruction(new MovInstruction(DEST_REG, returnStatReg));

    internalState.deallocateStackSpace(internalState.getFunctionSymTable());
    internalState.addInstruction(new PopInstruction(PC));
  }

  public void visitStatementsListNode(InternalState internalState, List<StatementNode> statements) {
    for (StatementNode stat : statements) {
      stat.generateAssembly(internalState);
    }
  }

  public void visitArrayElemNode(InternalState internalState, ArrayElemNode node) {
    Register arrayReg = internalState.popFreeRegister();

    generateElemAddr(internalState, arrayReg, node);
    LdrType ldrType = (node.getType(node.getCurrSymTable()).getSize() == BYTE_SIZE) ? LDRSB : LDR;
    internalState.addInstruction(new LdrInstruction(ldrType, arrayReg, arrayReg));

    internalState.pushFreeRegister(arrayReg);
  }


  public void visitBinaryOpExprNode(InternalState internalState, ExpressionNode left,
                                    ExpressionNode right,
                                    Operator.BinOp operator) {
    left.generateAssembly(internalState);
    Register leftResult = internalState.popFreeRegister();

    right.generateAssembly(internalState);
    Register rightResult = internalState.popFreeRegister();

    Register destReg = leftResult;
    if (leftResult == rightResult) {
      leftResult = internalState.popRegFromStack();
      destReg = rightResult;
    }

    /* Generate assembly for expected binary operation */
    switch (operator) {
      case MUL:
        internalState.addInstruction(
            new SMullInstruction(destReg, rightResult, leftResult, rightResult, false));
        internalState.addInstruction(
            new CompareInstruction(
                rightResult, new Operand(destReg, new Shift(ASR, MUL_SHIFT))));
        internalState.addInstruction(new BranchInstruction(
            ConditionCode.NE, BL, OVERFLOW));
        break;
      case DIV:
        internalState.addInstruction(new MovInstruction(DEST_REG, leftResult));
        internalState.addInstruction(new MovInstruction(ARG_REG_1, rightResult));
        internalState.addInstruction(
            new BranchInstruction(BL, DIV_ZERO));
        internalState.addInstruction(
            new BranchInstruction(BL, IDIV.getLabel()));
        internalState.addInstruction(new MovInstruction(destReg, DEST_REG));
        break;
      case MOD:
        internalState.addInstruction(new MovInstruction(DEST_REG, leftResult));
        internalState.addInstruction(new MovInstruction(ARG_REG_1, rightResult));
        internalState.addInstruction(
            new BranchInstruction(BL, DIV_ZERO));
        internalState.addInstruction(
            new BranchInstruction(BL, IDIVMOD.getLabel()));
        internalState.addInstruction(new MovInstruction(destReg, ARG_REG_1));
        break;
      case PLUS:
        internalState.addInstruction(
            new ArithmeticInstruction(ADD, destReg, leftResult,
                new Operand(rightResult), true));
        internalState.addInstruction(new BranchInstruction(VS, BL, OVERFLOW));
        break;
      case MINUS:
        internalState.addInstruction(
            new ArithmeticInstruction(SUB,
                destReg, leftResult, new Operand(rightResult), true));
        internalState.addInstruction(new BranchInstruction(VS, BL, OVERFLOW));
        break;
      case GREATER:
        conditionAssembly(internalState, destReg, leftResult, rightResult, GT, LE);
        break;
      case GEQ:
        conditionAssembly(internalState, destReg, leftResult, rightResult, GE, LT);
        break;
      case LESS:
        conditionAssembly(internalState, destReg, leftResult, rightResult, LT, GE);
        break;
      case LEQ:
        conditionAssembly(internalState, destReg, leftResult, rightResult, LE, GT);
        break;
      case EQUAL:
        conditionAssembly(internalState, destReg, leftResult, rightResult, EQ, NE);
        break;
      case NEQ:
        conditionAssembly(internalState, destReg, leftResult, rightResult, NE, EQ);
        break;
      case AND:
        internalState.addInstruction(
            new LogicalInstruction(AND, destReg, leftResult, new Operand(rightResult)));
        break;
      case OR:
        internalState.addInstruction(
            new LogicalInstruction(ORR, destReg, leftResult, new Operand(rightResult)));
    }

    if (destReg == rightResult) {
      internalState.pushFreeRegister(leftResult);
      internalState.pushFreeRegister(rightResult);
    } else {
      internalState.pushFreeRegister(rightResult);
      internalState.pushFreeRegister(leftResult);
    }
  }

  private void conditionAssembly(InternalState internalState, Register destReg, Register leftResult,
                                 Register rightResult, ConditionCode trueCond, ConditionCode falseCond) {
    internalState.addInstruction(new CompareInstruction(leftResult, new Operand(rightResult)));
    internalState.addInstruction(new MovInstruction(trueCond, destReg, TRUE));
    internalState.addInstruction(
        new MovInstruction(falseCond, destReg, FALSE));
  }

  public void visitBoolLiterExprNode(InternalState internalState, boolean value) {
    int intValue = value ? TRUE : FALSE;
    Register currDestination = internalState.peekFreeRegister();
    internalState.addInstruction(new MovInstruction(currDestination, intValue));
  }

  public void visitCharLiterExprNode(InternalState internalState, char value) {
    Register currDestination = internalState.peekFreeRegister();
    internalState.addInstruction(new MovInstruction(currDestination, value));
  }

  public void visitIdentifierNode(InternalState internalState, String identifier, DataTypeId type,
                                  SymbolTable currSymTable) {
    /* Get offset from symbolTable of variable and store that in available reg */
    if (currSymTable == null) {
      return;
    }
    Identifier id = currSymTable.lookupAll(identifier);
    if (id == null) {
      return;
    }

    int offset = currSymTable.getOffset(identifier);
    Register reg = internalState.peekFreeRegister();
    LdrType ldrInstr = (type.getSize() == BYTE_SIZE) ? LDRSB : LDR;
    internalState.addInstruction(new LdrInstruction(ldrInstr, reg, SP, offset));
  }

  public void visitIntLiterExprNode(InternalState internalState, int value) {
    Register currDestination = internalState.peekFreeRegister();
    internalState.addInstruction(new LdrInstruction(LDR, currDestination, value));
  }

  public void visitPairLiterExprNode(InternalState internalState) {
    Register currDestination = internalState.peekFreeRegister();
    internalState.addInstruction(new LdrInstruction(LDR, currDestination, NULL));
  }

  public void visitParenthesisExprNode(InternalState internalState, ExpressionNode innerExpr) {
    innerExpr.generateAssembly(internalState);
  }

  public void visitStringLiterNode(InternalState internalState, String value) {
    Register currDestination = internalState.peekFreeRegister();
    internalState.addInstruction(new LdrInstruction(LDR, currDestination,
        new MsgInstruction(value)));
  }

  public void visitUnaryOpExprNode(InternalState internalState, ExpressionNode operand,
                                   Operator.UnOp operator) {
    /* Load needed variable from stack */
    operand.generateAssembly(internalState);
    Register operandResult = internalState.popFreeRegister();

    /* Generate assembly for expected unary operation */
    switch (operator) {
      case NOT:
        internalState.addInstruction(
            new LogicalInstruction(EOR, operandResult, operandResult, new Operand(TRUE)));
        break;
      case NEGATION:
        internalState.addInstruction(
            new ArithmeticInstruction(RSB, operandResult, operandResult, new Operand(ZERO_OFFSET),
                SET_BITS));
        internalState.addInstruction(new BranchInstruction(ConditionCode.VS, BL, OVERFLOW));
        break;
      case LEN:
        internalState.addInstruction(new LdrInstruction(LdrType.LDR, operandResult, operandResult));
        break;
    }

    internalState.pushFreeRegister(operandResult);
  }

  public void visitArrayLiterNode(InternalState internalState, List<ExpressionNode> expressions,
                                  AssignRHSNode node) {
    SymbolTable currSymTable = node.getCurrSymTable();

    /* Get the size of the array elements type */
    DataTypeId type = ((ArrayType) node.getType(currSymTable)).getElemType();
    int arrElemSize = (type != null) ? type.getSize() : 0;

    /* Load array size in DEST_REG */
    int arrSize = expressions.size() * arrElemSize + INT_BYTES_SIZE;
    internalState.addInstruction(new LdrInstruction(LDR, DEST_REG, arrSize));

    /* Allocate space on the heap */
    internalState.addInstruction(new BranchInstruction(L, B, MALLOC.getLabel()));

    Register reg = internalState.popFreeRegister();
    internalState.addInstruction(new MovInstruction(reg, DEST_REG));

    /* If the array elem size is 1 byte, use STRB, otherwise use STR */
    StrType strInstr = (arrElemSize == 1) ? STRB : STR;

    /* Generate assembly for each of the expressions and store them */
    Register nextAvailable = internalState.peekFreeRegister();
    int i = INT_BYTES_SIZE;
    for (ExpressionNode expression : expressions) {
      expression.generateAssembly(internalState);
      internalState.addInstruction(new StrInstruction(strInstr, nextAvailable, reg, i));
      i += arrElemSize;
    }

    Register noOfArrElemsReg = internalState.peekFreeRegister();
    internalState
        .addInstruction(new LdrInstruction(LDR, noOfArrElemsReg, expressions.size()));
    internalState.addInstruction(new StrInstruction(STR, noOfArrElemsReg, reg));

    /* Push back and free nextAvailable allocation register */
    internalState.pushFreeRegister(reg);
  }

  public void visitFuncCallNode(InternalState internalState, IdentifierNode identifier,
                                List<ExpressionNode> arguments,
                                SymbolTable currSymTable) {
    /* Calculate total arguments size in argsTotalSize */
    int argsTotalSize = 0;
    int newOff = 0;
    //Map<String, Integer> offsetPerVarMap = currSymTable.saveOffsetPerVar();
    /* Arguments are stored in decreasing order they are given in the code */
    for (int i = arguments.size() - 1; i >= 0; i--) {
      /* Get argument, calculate size and add it to argsTotalSize */
      ExpressionNode currArg = arguments.get(i);

      /* Generate assembly code for the current argument */
      currArg.generateAssembly(internalState);

      int argSize = currArg.getType(currSymTable).getSize();

      StrType strInstr = (argSize == 1) ? STRB : StrType.STR;

      /* Store currArg on the stack and decrease stack pointer (stack grows downwards) */
      internalState.addInstruction(new StrInstruction(strInstr, internalState.peekFreeRegister(),
          SP, -argSize, true));
      argsTotalSize += argSize;

      currSymTable.incrementArgsOffset(argSize);
      //internalState.decrementStackOffset(argSize, internalState.getVarSize()
      //    + internalState.getStackOffset() - argSize);

    }
    currSymTable.resetArgsOffset();
    //currSymTable.setOffsetPerVar(offsetPerVarMap);
    //internalState
    //    .resetParamStackOffset(internalState.getStackOffset() + argsTotalSize + newOff);
    /* Branch Instruction to the callee label */
    String functionLabel = "f_" + identifier.toString();
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

  public void visitNewPairNode(InternalState internalState, ExpressionNode fstExpr,
                               ExpressionNode sndExpr,
                               SymbolTable currSymTable) {
    internalState.addInstruction(
        new LdrInstruction(LdrType.LDR, DEST_REG, NUM_PAIR_ELEMS * ADDRESS_BYTE_SIZE));

    /* Allocate space on the heap */
    internalState.addInstruction(new BranchInstruction(ConditionCode.L, B, MALLOC.getLabel()));

    Register pairReg = internalState.popFreeRegister();
    internalState.addInstruction(new MovInstruction(pairReg, DEST_REG));

    /* Generates the assembly code for each pair element*/
    generateElem(fstExpr, internalState, currSymTable, pairReg, FST);
    generateElem(sndExpr, internalState, currSymTable, pairReg, SND);

    internalState.pushFreeRegister(pairReg);
  }

  /* Helper function for visit newPairNode.
     Generates the assembly code for a pair element*/
  private void generateElem(ExpressionNode expr, InternalState internalState,
                            SymbolTable currSymTable,
                            Register pairReg, int offset) {
    /* Visit and generate assembly code for the expression */
    expr.generateAssembly(internalState);
    Register exprReg = internalState.popFreeRegister();

    /* Load expr type size into DEST_REG */
    int size = expr.getType(currSymTable).getSize();
    internalState.addInstruction(new LdrInstruction(LDR, DEST_REG, size));

    /* Allocate space on the heap */
    internalState.addInstruction(new BranchInstruction(ConditionCode.L, B, MALLOC.getLabel()));

    /* If the expr type size is 1 byte, use STRB, otherwise use STR */
    StrType strInstr = (size == BYTE_SIZE) ? STRB : StrType.STR;
    internalState.addInstruction(new StrInstruction(strInstr, exprReg, DEST_REG));

    internalState.addInstruction(
        new StrInstruction(StrType.STR, DEST_REG, pairReg, offset * ADDRESS_BYTE_SIZE));

    internalState.pushFreeRegister(exprReg);
  }

  public void visitPairElemNode(InternalState internalState, ExpressionNode expression,
                                int position,
                                SymbolTable currSymTable) {
    /* Visit and generate assembly code for the expression */
    expression.generateAssembly(internalState);
    Register reg = internalState.peekFreeRegister();

    internalState.addInstruction(new MovInstruction(DEST_REG, reg));

    /* Check for null pointer exception */
    internalState.addInstruction(new BranchInstruction(BL, NULL_POINTER));
    internalState.addInstruction(
        new LdrInstruction(LdrType.LDR, reg, reg, position * ADDRESS_BYTE_SIZE));

    /* Calculate type of Ldr instruction based on the size of the pair elem */
    PairType pair = (PairType) expression.getType(currSymTable);
    DataTypeId type = (position == FST) ? pair.getFstType() : pair.getSndType();
    int elemSize = type.getSize();
    LdrType ldrInstr = (elemSize == 1) ? LdrType.LDRSB : LdrType.LDR;

    internalState.addInstruction(new LdrInstruction(ldrInstr, reg, reg));
  }
}
