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
import InternalRepresentation.Enums.*;
import InternalRepresentation.Instructions.*;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.Operator;
import SemanticAnalysis.SymbolTable;

import java.util.List;
import java.util.Map;

import static InternalRepresentation.Enums.ConditionCode.*;
import static InternalRepresentation.Enums.Directive.*;
import static InternalRepresentation.Enums.LdrType.*;
import static InternalRepresentation.Enums.Register.*;

public class CodeGenVisitor {

    private static final int FST = 0;
    private static final int FALSE = 0;
    private static final int SND = 1;
    private static final int TRUE = 1;
    private static final int BYTE_SIZE = 1;
    private static final int NO_OF_ELEMS = 2;
    private static final int NO_OFFSET = 0;
    private static final int RETURN_ADDRESS_SIZE = 4;
    private static final int MUL_SHIFT = 31;
    private static final int INT_BYTES_SIZE = 4;
    private static final int ADDRESS_BYTE_SIZE = 4;
    private static final int MAX_DEALLOCATION_SIZE = 1024;
    private static final boolean SET_BITS = true;

    public CodeGenVisitor() {

    }

    public void visitProgramNode(InternalState internalState, StatementNode statementNode,
                                 List<FunctionNode> functionNodes) {
        for (FunctionNode functionNode : functionNodes) {
            functionNode.generateAssembly(internalState);
        }

        // add main label and push Link Register
        internalState.addInstruction(new LabelInstruction("main"));
        internalState.addInstruction(new PushInstruction(Register.LR));

        //TODO allocate stack space for variables. How to get the var symbol table??
        // TODO size should include function calls params sizes ???????
        if (statementNode.getCurrSymTable() != null)
            internalState.allocateStackSpace(statementNode.getCurrSymTable());

        statementNode.generateAssembly(internalState);

        if (statementNode.getCurrSymTable() != null) {
            internalState.deallocateStackSpace(statementNode.getCurrSymTable());
        }

        internalState.addInstruction(new LdrInstruction(LdrType.LDR, Register.DEST_REG, NO_OFFSET));
        internalState.addInstruction(new PopInstruction(Register.PC));
        internalState.addInstruction(new DirectiveInstruction(Directive.LTORG));
    }

    public void visitFunctionNode(InternalState internalState, IdentifierNode identifier, ParamListNode params,
                                  StatementNode bodyStatement, SymbolTable currSymTable) {
        internalState.resetAvailableRegs();

        internalState.addInstruction(new LabelInstruction("f_" + identifier.getIdentifier()));
        internalState.addInstruction(new PushInstruction(LR));
        internalState.allocateStackSpace(currSymTable);
        params.generateAssembly(internalState);
        internalState.setFunctionSymTable(currSymTable);
        bodyStatement.generateAssembly(internalState);
        internalState.resetParamStackOffset();
        internalState.addInstruction(new PopInstruction(PC));
        internalState.addInstruction(new DirectiveInstruction(LTORG));
    }

    public void visitParamListNode(InternalState internalState, List<IdentifierNode> identifiers, SymbolTable currSymTable) {
        int varOffset = internalState.getArgStackOffset();
        internalState.incrementParamStackOffset(RETURN_ADDRESS_SIZE);
        for (IdentifierNode identifier : identifiers) {
            int paramSize = identifier.getType(currSymTable).getSize();
            currSymTable.setOffset(identifier.getIdentifier(), internalState.getArgStackOffset()
                    + internalState.getParamStackOffset());
            internalState.incrementParamStackOffset(paramSize);
        }
    }

    public void visitAssignVarNode(InternalState internalState, AssignLHSNode left, AssignRHSNode right,
                                   SymbolTable currSymTable) {
        right.generateAssembly(internalState);
        Register rightNodeResult = internalState.popFreeRegister();

        if (left instanceof IdentifierNode) {
            int typeSize = left.getType(currSymTable).getSize();
            int offset = currSymTable.getOffset(((IdentifierNode) left).getIdentifier());

            StrType strType = typeSize == BYTE_SIZE ? StrType.STRB : StrType.STR;
            internalState
                    .addInstruction(new StrInstruction(strType, rightNodeResult, Register.SP, offset));

        } else if (left instanceof PairElemNode) {
            PairElemNode pairElem = (PairElemNode) left;
            Register leftNodeResult = internalState.peekFreeRegister();

            String pairID = pairElem.getIdentifier();
            int offset = currSymTable
                    .getOffset(pairID); 

            internalState
                    .addInstruction(new LdrInstruction(LdrType.LDR, leftNodeResult, Register.SP, offset));
            internalState.addInstruction(new MovInstruction(Register.DEST_REG, leftNodeResult));

            internalState
                    .addInstruction(new BranchInstruction(BranchOperation.BL, BuiltInFunction.NULL_POINTER));

            internalState.addInstruction(new LdrInstruction(LdrType.LDR, leftNodeResult, leftNodeResult,
                    pairElem.getPosition() * ADDRESS_BYTE_SIZE));

            StrType strType =
                    pairElem.getType(currSymTable).getSize() == BYTE_SIZE ? StrType.STRB : StrType.STR;
            internalState.addInstruction(new StrInstruction(strType, rightNodeResult, leftNodeResult));

        } else if (left instanceof ArrayElemNode) {
            Register arrayReg = internalState.popFreeRegister();

            ArrayElemNode arrayElem = (ArrayElemNode) left;
            arrayElem.generateElemAddr(internalState, arrayReg);

            StrType strType =
                    arrayElem.getType(currSymTable).getSize() == BYTE_SIZE ? StrType.STRB : StrType.STR;
            internalState.addInstruction(
                    new StrInstruction(strType, rightNodeResult, arrayReg));

            internalState.pushFreeRegister(arrayReg);
        }

        internalState.pushFreeRegister(rightNodeResult);
    }

    public void visitDeclarationStatementNode(InternalState internalState, AssignRHSNode assignment, TypeNode type,
                                              IdentifierNode identifier, SymbolTable currSymTable) {
        assignment.generateAssembly(internalState);

        int typeSize = type.getType().getSize();
        StrType storeType = typeSize == BYTE_SIZE ? StrType.STRB : StrType.STR;

        internalState.decrementArgStackOffset(typeSize);
        currSymTable.setOffset(identifier.getIdentifier(), internalState.getArgStackOffset());

        Register destReg = internalState.peekFreeRegister();

        internalState.addInstruction(new StrInstruction(storeType, destReg, Register.SP,
                currSymTable.getOffset(identifier.getIdentifier())));
    }

    public void visitExitStatementNode(InternalState internalState, ExpressionNode expression) {
        Register exitCodeReg = internalState.peekFreeRegister();

        expression.generateAssembly(internalState);
        internalState.addInstruction(new MovInstruction(Register.DEST_REG, exitCodeReg));
        internalState.addInstruction(new BranchInstruction(BranchOperation.BL, "exit"));
    }

    public void visitFreeStatementNode(InternalState internalState, ExpressionNode expression) {
        expression.generateAssembly(internalState);
        internalState.addInstruction(new MovInstruction(DEST_REG, internalState.peekFreeRegister()));
        internalState
                .addInstruction(new BranchInstruction(BranchOperation.BL, BuiltInFunction.FREE_PAIR));
    }

    public void visitIfStatementNode(InternalState internalState, ExpressionNode condition, StatementNode thenStatement,
                                     StatementNode elseStatement) {
        condition.generateAssembly(internalState);

        String elseLabel = internalState.generateNewLabel();
        String endIfLabel = internalState.generateNewLabel();

        internalState.addInstruction(
                new CompareInstruction(internalState.peekFreeRegister(), new Operand(0)));
        internalState.addInstruction(
                new BranchInstruction(ConditionCode.EQ, BranchOperation.B, elseLabel));

        internalState.allocateStackSpace(thenStatement.getCurrSymTable());
        thenStatement.generateAssembly(internalState);
        internalState.deallocateStackSpace(thenStatement.getCurrSymTable());

        generateCondInstruction(internalState, elseStatement, endIfLabel, elseLabel);
    }

    public void visitWhileStatementNode(InternalState internalState, ExpressionNode condition, StatementNode statement) {
        String condLabel = internalState.generateNewLabel();
        String statementLabel = internalState.generateNewLabel();

        generateCondInstruction(internalState, statement, condLabel, statementLabel);
        condition.generateAssembly(internalState);

        internalState
                .addInstruction(new CompareInstruction(internalState.peekFreeRegister(), new Operand(TRUE)));
        internalState.addInstruction(new BranchInstruction(
                ConditionCode.EQ, BranchOperation.B, statementLabel));
    }

    private void generateCondInstruction(InternalState internalState, StatementNode statement, String condLabel, String statementLabel) {
        internalState.addInstruction(new BranchInstruction(BranchOperation.B, condLabel));

        internalState.addInstruction(new LabelInstruction(statementLabel));
        internalState.allocateStackSpace(statement.getCurrSymTable());
        statement.generateAssembly(internalState);
        internalState.deallocateStackSpace(statement.getCurrSymTable());
        internalState.addInstruction(new LabelInstruction(condLabel));
    }

    public void visitNewScopeStatementNode(InternalState internalState, StatementNode statement) {
        internalState.allocateStackSpace(statement.getCurrSymTable());
        statement.generateAssembly(internalState);
        internalState.deallocateStackSpace(statement.getCurrSymTable());
    }

    public void visitPrintLineStatementNode(InternalState internalState, ExpressionNode expression,
                                            SymbolTable currSymTable) {
        visitPrintStatementNode(internalState, expression, currSymTable);

        internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                BuiltInFunction.PRINT_LN));
    }

    public void visitPrintStatementNode(InternalState internalState, ExpressionNode expression,
                                        SymbolTable currSymTable) {
        expression.generateAssembly(internalState);
        Register nextAvailable = internalState.peekFreeRegister();

        internalState.addInstruction(new MovInstruction(Register.DEST_REG, nextAvailable));

        DataTypeId type = expression.getType(currSymTable);

        if (type instanceof ArrayType) {
            if (((ArrayType) type).getElemType().equals(new BaseType(BaseType.Type.CHAR))) {
                internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                        BuiltInFunction.PRINT_STRING));
            } else {
                internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                        BuiltInFunction.PRINT_REFERENCE));
            }
        } else if (type instanceof PairType) {
            internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                    BuiltInFunction.PRINT_REFERENCE));
        } else if (type instanceof BaseType) {
            BaseType.Type baseType = ((BaseType) type).getBaseType();
            switch (baseType) {
                case CHAR:
                    internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                            SystemBuiltInFunction.PUTCHAR.getMessage()));
                    break;
                case STRING:
                    internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                            BuiltInFunction.PRINT_STRING));
                    break;
                case BOOL:
                    internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                            BuiltInFunction.PRINT_BOOL));
                    break;
                case INT:
                    internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                            BuiltInFunction.PRINT_INT));
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
            internalState.addInstruction(new ArithmeticInstruction(ArithmeticOperation.ADD, nextAvailable, Register.SP,
                    new Operand(offset), !SET_BITS));
        } else {
            assignment.generateAssembly(internalState);
            internalState.addInstruction(new ArithmeticInstruction(ArithmeticOperation.ADD, nextAvailable, Register.SP,
                    new Operand(NO_OFFSET), !SET_BITS));
        }

        internalState.addInstruction(new MovInstruction(Register.DEST_REG, nextAvailable));
        DataTypeId type = assignment.getType(currSymTable);

        if (type instanceof BaseType) {
            BaseType.Type baseType = ((BaseType) type).getBaseType();

            switch (baseType) {
                case INT:
                    internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                            BuiltInFunction.READ_INT));
                    break;
                case CHAR:
                    internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
                            BuiltInFunction.READ_CHAR));
                    break;
            }
        }
    }

    public void visitReturnStatementNode(InternalState internalState, ExpressionNode returnExpr) {
        Register returnStatReg = internalState.peekFreeRegister();
        returnExpr.generateAssembly(internalState);


        internalState.addInstruction(new MovInstruction(Register.DEST_REG, returnStatReg));

        internalState.deallocateStackSpace(internalState.getFunctionSymTable());
        internalState.addInstruction(new PopInstruction(Register.PC));
    }

    public void visitStatementsListNode(InternalState internalState, List<StatementNode> statements) {
        for (StatementNode stat : statements) {
            stat.generateAssembly(internalState);
        }
    }

    public void visitArrayElemNode(InternalState internalState, ArrayElemNode node) {
        Register arrayReg = internalState.popFreeRegister();

        node.generateElemAddr(internalState, arrayReg);
        internalState.addInstruction(new LdrInstruction(LDR, arrayReg, arrayReg));

        internalState.pushFreeRegister(arrayReg);
    }


    public void visitBinaryOpExprNode(InternalState internalState, ExpressionNode left, ExpressionNode right,
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

        switch (operator) {
            case MUL:
                internalState.addInstruction(
                        new SMullInstruction(destReg, rightResult, leftResult, rightResult, false));
                internalState.addInstruction(
                        new CompareInstruction(
                                rightResult, new Operand(destReg, new Shift(ShiftType.ASR, MUL_SHIFT))));
                internalState.addInstruction(new BranchInstruction(
                        ConditionCode.NE,
                        BranchOperation.BL, BuiltInFunction.OVERFLOW));
                break;
            case DIV:
                internalState.addInstruction(new MovInstruction(Register.DEST_REG, leftResult));
                internalState.addInstruction(new MovInstruction(R1, rightResult));
                internalState.addInstruction(
                        new BranchInstruction(BranchOperation.BL, BuiltInFunction.DIV_ZERO));
                internalState.addInstruction(
                        new BranchInstruction(BranchOperation.BL, SystemBuiltInFunction.IDIV.getMessage()));
                internalState.addInstruction(new MovInstruction(destReg, Register.DEST_REG));
                break;
            case MOD:
                internalState.addInstruction(new MovInstruction(Register.DEST_REG, leftResult));
                internalState.addInstruction(new MovInstruction(R1, rightResult));
                internalState.addInstruction(
                        new BranchInstruction(BranchOperation.BL, BuiltInFunction.DIV_ZERO));
                internalState.addInstruction(
                        new BranchInstruction(BranchOperation.BL, SystemBuiltInFunction.IDIVMOD.getMessage()));
                internalState.addInstruction(new MovInstruction(destReg, R1));
                break;
            case PLUS:
                internalState.addInstruction(
                        new ArithmeticInstruction(ArithmeticOperation.ADD,
                                destReg, leftResult, new Operand(rightResult), true));
                internalState.addInstruction(new BranchInstruction(VS,
                        BranchOperation.BL, BuiltInFunction.OVERFLOW));
                break;
            case MINUS:
                internalState.addInstruction(
                        new ArithmeticInstruction(ArithmeticOperation.SUB,
                                destReg, leftResult, new Operand(rightResult), true));
                internalState.addInstruction(new BranchInstruction(VS,
                        BranchOperation.BL, BuiltInFunction.OVERFLOW));
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
                        new LogicalInstruction(
                                LogicalOperation.AND, destReg, leftResult, new Operand(rightResult)));
                break;
            case OR:
                internalState.addInstruction(
                        new LogicalInstruction(
                                LogicalOperation.ORR, destReg, leftResult, new Operand(rightResult)));
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
                                    SymbolTable currSymTable) { //TODO: PASS TYPE/NODE???
        // get offset from symbolTable of variable and store that in available reg
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
        internalState.addInstruction(new LdrInstruction(ldrInstr, reg, Register.SP, offset));
    }

    public void visitIntLiterExprNode(InternalState internalState, int value) {
        Register currDestination = internalState.peekFreeRegister();
        internalState.addInstruction(new LdrInstruction(LdrType.LDR, currDestination, value));
    }

    public void visitPairLiterExprNode(InternalState internalState) {
        Register currDestination = internalState.peekFreeRegister();
        internalState.addInstruction(new LdrInstruction(LdrType.LDR, currDestination, NO_OFFSET));
    }

    public void visitParenthesisExprNode(InternalState internalState, ExpressionNode innerExpr) {
        innerExpr.generateAssembly(internalState);
    }

    public void visitStringLiterNode(InternalState internalState, String value) {
        Register currDestination = internalState.peekFreeRegister();
        internalState.addInstruction(new LdrInstruction(LdrType.LDR, currDestination,
                new MsgInstruction(value)));
    }

    public void visitUnaryOpExprNode(InternalState internalState, ExpressionNode operand, Operator.UnOp operator) {
        operand.generateAssembly(internalState);
        Register operandResult = internalState.popFreeRegister();

        switch (operator) {
            case NOT:
                internalState.addInstruction(new LogicalInstruction(LogicalOperation.EOR,
                        operandResult, operandResult, new Operand(TRUE)));
                break;
            case NEGATION:
                internalState.addInstruction(new ArithmeticInstruction(ArithmeticOperation.RSB,
                        operandResult, operandResult, new Operand(NO_OFFSET), true));
                internalState.addInstruction(new BranchInstruction(
                        ConditionCode.VS,
                        BranchOperation.BL, BuiltInFunction.OVERFLOW));
                break;
            case LEN:
                internalState.addInstruction(new LdrInstruction(LdrType.LDR, operandResult, operandResult));
                break;
            case ORD:
            case CHR:
        }

        internalState.pushFreeRegister(operandResult);
    }

    public void visitArrayLiterNode(InternalState internalState, List<ExpressionNode> expressions, AssignRHSNode node) {
        SymbolTable currSymTable = node.getCurrSymTable();

        // get the size of the array elements type
        DataTypeId type = ((ArrayType) node.getType(currSymTable)).getElemType();
        int arrElemSize = (type != null) ? type.getSize() : 0;

        // load array size in R0
        int arrSize = expressions.size() * arrElemSize + INT_BYTES_SIZE;
        internalState.addInstruction(new LdrInstruction(LdrType.LDR, Register.DEST_REG, arrSize));

        // BL malloc
        internalState.addInstruction(new BranchInstruction(ConditionCode.L,
                BranchOperation.B, "malloc"));

        Register reg = internalState.popFreeRegister();
        internalState.addInstruction(new MovInstruction(reg, Register.DEST_REG));

        // if the array elem size is 1 byte, use STRB, otherwise use STR
        StrType strInstr = (arrElemSize == 1) ? StrType.STRB : StrType.STR;

        //iterate over the expressions, generate assembly for them and store them
        Register nextAvailable = internalState.peekFreeRegister();
        int i = INT_BYTES_SIZE;
        for (ExpressionNode expression : expressions) {
            expression.generateAssembly(internalState);
            internalState.addInstruction(new StrInstruction(strInstr, nextAvailable, reg, i));
            i += arrElemSize;
        }

        Register noOfArrElemsReg = internalState.peekFreeRegister();
        internalState
                .addInstruction(new LdrInstruction(LdrType.LDR, noOfArrElemsReg, expressions.size()));
        internalState.addInstruction(new StrInstruction(StrType.STR, noOfArrElemsReg, reg));

        //push back and free nextAvailable allocation register
        internalState.pushFreeRegister(reg);
    }

    public void visitFuncCallNode(InternalState internalState, IdentifierNode identifier, List<ExpressionNode> arguments,
                           SymbolTable currSymTable) {
        // calculate total arguments size in argsTotalSize
        int argsTotalSize = 0;
        int newOff = 0;
        Map<String, Integer> offsetPerVarMap = currSymTable.saveOffsetPerVar();
        // arguments are stored in decreasing order they are given in the code
        for (int i = arguments.size() - 1; i >= 0; i--) {
            // get argument, calculate size and add it to argsTotalSize
            ExpressionNode currArg = arguments.get(i);

            // generate assembly code for the current argument
            currArg.generateAssembly(internalState);

            int argSize = currArg.getType(currSymTable).getSize();

            StrType strInstr = (argSize == 1) ? StrType.STRB : StrType.STR;

            //store currArg on the stack and decrease stack pointer (stack grows downwards)
            internalState.addInstruction(new StrInstruction(strInstr, internalState.peekFreeRegister(),
                    Register.SP, -argSize, true));
            argsTotalSize += argSize;
            newOff += internalState.decrementParamStackOffset(argSize, internalState.getVarSize()
                    + internalState.getParamStackOffset() - argSize);

        }
        currSymTable.setOffsetPerVar(offsetPerVarMap);
        internalState.resetParamStackOffset(internalState.getParamStackOffset() + argsTotalSize + newOff);
        //Branch Instruction to the callee label
        String functionLabel = "f_" + identifier.toString();
        internalState.addInstruction(new BranchInstruction(
                ConditionCode.L, BranchOperation.B, functionLabel));

        //de-allocate stack from the function arguments. Max size for one de-allocation is 1024B
        while (argsTotalSize > 0) {
            internalState.addInstruction(
                    new ArithmeticInstruction(ArithmeticOperation.ADD, Register.SP, Register.SP,
                            new Operand(Math.min(argsTotalSize, MAX_DEALLOCATION_SIZE)),
                            false));
            argsTotalSize -= Math.min(argsTotalSize, MAX_DEALLOCATION_SIZE);
        }

        //move the result stored in R0 in the first free register
        internalState
                .addInstruction(new MovInstruction(internalState.peekFreeRegister(), Register.DEST_REG));
    }

    public void visitNewPairNode(InternalState internalState, ExpressionNode fstExpr, ExpressionNode sndExpr,
                          SymbolTable currSymTable) {
        internalState.addInstruction(
                new LdrInstruction(LdrType.LDR, Register.DEST_REG, NO_OF_ELEMS * ADDRESS_BYTE_SIZE));
        //BL malloc
        internalState.addInstruction(
                new BranchInstruction(ConditionCode.L, BranchOperation.B, "malloc"));

        Register pairReg = internalState.popFreeRegister();

        internalState.addInstruction(new MovInstruction(pairReg, Register.DEST_REG));

        generateElem(fstExpr, internalState, currSymTable, pairReg, FST);
        generateElem(sndExpr, internalState, currSymTable, pairReg, SND);

        internalState.pushFreeRegister(pairReg);
    }

    private void generateElem(ExpressionNode expr, InternalState internalState, SymbolTable currSymTable,
                              Register pairReg, int offset) {
        /* begin expr code generation */
        expr.generateAssembly(internalState);
        Register exprReg = internalState.popFreeRegister();

        // load expr type size into R0
        int size = expr.getType(currSymTable).getSize();
        internalState.addInstruction(new LdrInstruction(LdrType.LDR, Register.DEST_REG, size));

        // BL malloc
        internalState.addInstruction(new BranchInstruction(
                ConditionCode.L, BranchOperation.B, "malloc"));

        StrType strInstr = (size == 1) ? StrType.STRB : StrType.STR;
        internalState.addInstruction(new StrInstruction(strInstr, exprReg, Register.DEST_REG));

        internalState.addInstruction(
                new StrInstruction(StrType.STR, Register.DEST_REG, pairReg, offset * ADDRESS_BYTE_SIZE));

        internalState.pushFreeRegister(exprReg);
    }

    public void visitPairElemNode(InternalState internalState, ExpressionNode expression, int position,
                           SymbolTable currSymTable) {
        expression.generateAssembly(internalState);
        Register reg = internalState.peekFreeRegister();

        internalState.addInstruction(new MovInstruction(Register.DEST_REG, reg));

        internalState
                .addInstruction(new BranchInstruction(BranchOperation.BL, BuiltInFunction.NULL_POINTER));
        internalState
                .addInstruction(new LdrInstruction(LdrType.LDR, reg, reg, position * ADDRESS_BYTE_SIZE));

        PairType pair = (PairType) expression.getType(currSymTable);
        DataTypeId type = (position == FST) ? pair.getFstType() : pair.getSndType();
        int elemSize = type.getSize();
        LdrType ldrInstr = (elemSize == 1) ? LdrType.LDRSB : LdrType.LDR;

        internalState.addInstruction(new LdrInstruction(ldrInstr, reg, reg));
    }
}
