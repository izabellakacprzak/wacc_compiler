import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.assignment.*;
import AbstractSyntaxTree.expression.*;
import AbstractSyntaxTree.statement.*;
import AbstractSyntaxTree.type.*;
import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.Operator;
import SemanticAnalysis.Operator.*;
import antlr.WACCParser;
import antlr.WACCParser.*;
import antlr.WACCParserBaseVisitor;
import java.util.ArrayList;
import java.util.List;

public class ASTVisitor extends WACCParserBaseVisitor<ASTNode> {

  /* PROGRAM NODE */
  /* Visits the program rule
     Returns a ProgramNode with the visited StatementNode representing the main program statement
     and a list of FunctionNode of all functions in the program */
  @Override
  public ASTNode visitProgram(ProgramContext ctx) {
    StatementNode statementNode = (StatementNode) visit(ctx.stat());
    List<FunctionNode> functionNodes = new ArrayList<>();
    for (FuncContext f : ctx.func()) {
      functionNodes.add((FunctionNode) visit(f));
    }
    return new ProgramNode(statementNode, functionNodes);
  }

  /* FUNCTION AND FUNCTION PARAMETERS NODES */

  /* Visits the function declaration rule
     Returns a FunctionNode with a typeNode representing the return type,
     IdentifierNode function name, ParamListNode list of all function parameters
     and StatementNode with the body statement */
  @Override
  public ASTNode visitFunc(FuncContext ctx) {
    TypeNode type = (TypeNode) visit(ctx.type());
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    IdentifierNode identifier = new IdentifierNode(line, charPositionInLine, ctx.IDENT().getText());
    StatementNode body = (StatementNode) visit(ctx.stat());
    ParamListNode params = new ParamListNode(new ArrayList<>(), new ArrayList<>());

    if (ctx.param_list() != null) {
      params = (ParamListNode) visit(ctx.param_list());
    }

    return new FunctionNode(type, identifier, params, body);
  }

  /* Visits the parameter list rule
     Returns a ParamListNode with a list of IdentifierNode parameter names
     and list of corresponding visited TypeNodes representing the types of the parameters */
  @Override
  public ASTNode visitParam_list(WACCParser.Param_listContext ctx) {
    List<IdentifierNode> names = new ArrayList<>();
    List<TypeNode> types = new ArrayList<>();

    if (ctx.IDENT() != null) {
      int line = ctx.getStart().getLine();
      int charPositionInLine = ctx.getStart().getCharPositionInLine();

      for (int i = 0; i < ctx.IDENT().size(); i++) {

        names.add(new IdentifierNode(line, charPositionInLine, ctx.IDENT(i).getText()));
        types.add((TypeNode) visit(ctx.type(i)));
      }
    }

    return new ParamListNode(names, types);
  }

  /* CLASS AND CLASS ELEMENTS NODES */

  @Override
  public ASTNode visitClassdecl(ClassdeclContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    IdentifierNode className = new IdentifierNode(line, charPositionInLine, ctx.IDENT().getText());

    List<AttributeNode> attributes = new ArrayList<>();
    List<ConstructorNode> constructors = new ArrayList<>();
    List<FunctionNode> methods = new ArrayList<>();

    for(int i = 0; i < ctx.attribute().size(); i++) {
      attributes.add((AttributeNode) (visit(ctx.attribute(i))));
    }
    for(int i = 0; i < ctx.constructor().size(); i++) {
      constructors.add((ConstructorNode) (visit(ctx.constructor(i))));
    }
    for(int i = 0; i < ctx.func().size(); i++) {
      methods.add((FunctionNode) (visit(ctx.func(i))));
    }
    return new ClassNode(className, attributes, constructors, methods);
  }

  @Override
  public ASTNode visitAttrNoAssign(AttrNoAssignContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    IdentifierNode name = new IdentifierNode(line, charPositionInLine, ctx.IDENT().getText());
    TypeNode type = (TypeNode) visit(ctx.type());

    return new AttributeNode(name, type);
  }

  @Override
  public ASTNode visitAttrAssign(AttrAssignContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    IdentifierNode name = new IdentifierNode(line, charPositionInLine, ctx.IDENT().getText());
    TypeNode type = (TypeNode) visit(ctx.type());
    AssignRHSNode assignment = (AssignRHSNode) visit(ctx.assign_rhs());

    return new AttributeNode(name, type, assignment);
  }

  @Override
  public ASTNode visitConstructor(ConstructorContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    IdentifierNode name = new IdentifierNode(line, charPositionInLine, ctx.IDENT().getText());
    ParamListNode parameters = (ParamListNode) visit(ctx.param_list());
    StatementNode body = (StatementNode) visit(ctx.stat());

    return new ConstructorNode(name, parameters, body);
  }

  @Override
  public ASTNode visitAttributeExpr(AttributeExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    IdentifierNode objectName = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT(0).getText());
    IdentifierNode attributeName = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT(1).getText());

    return new AttributeExprNode(line, charPositionInLine, objectName, attributeName);
  }

  @Override
  public ASTNode visitMethodCallRHS(MethodCallRHSContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    IdentifierNode objectName = new IdentifierNode(line, charPositionInLine, ctx.IDENT(0).getText());
    IdentifierNode methodName = new IdentifierNode(line, charPositionInLine, ctx.IDENT(1).getText());
    List<ExpressionNode> expressions = new ArrayList<>();

    for(int i = 0; i < ctx.expr().size(); i++) {
      expressions.add((ExpressionNode) visit(ctx.expr(i)));
    }

    return new MethodCallNode(line, charPositionInLine, objectName, methodName, expressions);
  }

  @Override
  public ASTNode visitObjectDeclStat(ObjectDeclStatContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    IdentifierNode className = new IdentifierNode(line, charPositionInLine, ctx.IDENT(0).getText());
    IdentifierNode objectName = new IdentifierNode(line, charPositionInLine, ctx.IDENT(1).getText());
    List<ExpressionNode> expressions = new ArrayList<>();

    for(int i = 0; i < ctx.expr().size(); i++) {
      expressions.add((ExpressionNode) visit(ctx.expr(i)));
    }

    return new ObjectDeclStatementNode(className, objectName, expressions);
  }

  /* RHS ASSIGNMENT NODES */

  /* Visits the RHS assignment expression rule
     Returns an ExpressionNode corresponding to expression being assigned */
  @Override
  public ASTNode visitExprRHS(ExprRHSContext ctx) {
    return visit(ctx.expr());
  }

  /* Visits the array literal RHS assignment rule
     Returns an ArrayLiterNode with the corresponding line number and character position
     for semantic error messages and a list of visited ExpressionNode
     corresponding to array elements*/
  @Override
  public ASTNode visitArrayLiterRHS(ArrayLiterRHSContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    List<ExpressionNode> expressionNodes = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      expressionNodes.add((ExpressionNode) visit(e));
    }
    return new ArrayLiterNode(line, charPositionInLine, expressionNodes);
  }

  /* Visits the newpair RHS assignment rule
     Returns a NewPairNode with the corresponding line number and character position
     for semantic error messages, the visited left and right ExpressionNode */
  @Override
  public ASTNode visitNewPairRHS(NewPairRHSContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode leftExpr = (ExpressionNode) visit(ctx.expr(0));
    ExpressionNode rightExpr = (ExpressionNode) visit(ctx.expr(1));

    return new NewPairNode(line, charPositionInLine, leftExpr, rightExpr);
  }

  /* Visits the pair element RHS assignment rule
     If corresponds to the FST element of a pair call visitFstPairExpr
     otherwise calls visitSndPairExpr and returns a PairElemNode */
  @Override
  public ASTNode visitPairElemRHS(PairElemRHSContext ctx) {
    return visit(ctx.pair_elem());
  }

  /* Visits the fst pair element expression rule
     Returns a PairElemNode with the corresponding line number and character position
     for semantic error messages and a visited ExpressionNode for the fst element expression */
  @Override
  public ASTNode visitFstPairExpr(FstPairExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());

    return new PairElemNode(line, charPositionInLine, 0, expressionNode);
  }

  /* Visits the snd pair element expression rule
     Returns a PairElemNode with the corresponding line number and character position
     for semantic error messages and a visited ExpressionNode for the snd element expression */
  @Override
  public ASTNode visitSndPairExpr(SndPairExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());

    return new PairElemNode(line, charPositionInLine, 1, expressionNode);
  }

  /* Visits the function call RHS assignment rule
     Returns a FuncCallNode with the corresponding line number and character position
     for semantic error messages, an IdentifierNode function ID
     and a list of visited ExpressionNode corresponding to the arguments of the function */
  @Override
  public ASTNode visitFuncCallRHS(FuncCallRHSContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    IdentifierNode identifierNode = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT().getText());
    List<ExpressionNode> arguments = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      arguments.add((ExpressionNode) visit(e));
    }
    return new FuncCallNode(line, charPositionInLine, identifierNode, arguments);
  }

  /* LHS ASSIGNMENT NODES */

  /* Visits the identifier LHS assignment rule
     Returns a IdentifierNode with the corresponding line number and character position
     for semantic error messages and the corresponding ID string */
  @Override
  public ASTNode visitIdentLHS(IdentLHSContext ctx) {

    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new IdentifierNode(line, charPositionInLine, ctx.IDENT().getText());
  }

  /* Visits the array element LHS assignment rule
     Calls visitArray_elem and returns an ArrayElemNode */
  @Override
  public ASTNode visitArrayElemLHS(ArrayElemLHSContext ctx) {
    return visit(ctx.array_elem());
  }

  /* Visits the array element rule
     Returns an ArrayElemNode with the corresponding line number and character position
     for semantic error messages, an IdentifierNode corresponding to the array ID
     and a list of ExpressionNode corresponding to the array element's indexes */
  @Override
  public ASTNode visitArray_elem(Array_elemContext ctx) {

    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    IdentifierNode identifierNode = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT().getText());

    List<ExpressionNode> expressions = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      expressions.add((ExpressionNode) visit(e));
    }
    return new ArrayElemNode(line, charPositionInLine, identifierNode, expressions);
  }

  /* Visits the pair element LHS assignment rule
     If corresponds to the FST element of a pair call visitFstPairExpr
     otherwise calls visitSndPairExpr and returns a PairElemNode */
  @Override
  public ASTNode visitPairElemLHS(PairElemLHSContext ctx) {
    return visit(ctx.pair_elem());
  }

  /* EXPRESSION NODES */

  /* Visits the array element expression rule
     If corresponds to the FST element of a pair call visitFstPairExpr
     otherwise calls visitSndPairExpr and returns a PairElemNode */
  @Override
  public ASTNode visitArrayElemExpr(ArrayElemExprContext ctx) {
    return visit(ctx.array_elem());
  }

  /* Visits the binary operator expression rule
     Returns a BinaryOpExprNode with the corresponding line number and character position
     for semantic error messages, a left and right expression ExpressionNode and an operator */
  @Override
  public ASTNode visitBinaryExpr(BinaryExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    ExpressionNode leftExpr = (ExpressionNode) visit(ctx.expr(0));
    ExpressionNode rightExpr = (ExpressionNode) visit(ctx.expr(1));

    /* returns the operator enum based on the input string */
    BinOp operator = Operator.BinOp.valueOfLabel(ctx.op.getText());

    /* if the given operator doesn't correspond to any known throws an exception */
    if (operator == null) {
      throw new IllegalArgumentException("Invalid Binary Operator");
    }

    return new BinaryOpExprNode(line, charPositionInLine, leftExpr, rightExpr, operator);
  }

  /* Visits the bool literal expression rule
     Returns a BoolLiterExprNode with the corresponding line number and character position
     for semantic error messages and a boolean value (true | false) */
  @Override
  public ASTNode visitBoolLiterExpr(BoolLiterExprContext ctx) {

    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new BoolLiterExprNode(line, charPositionInLine,
        Boolean.parseBoolean(ctx.BOOL_LITER().getText()));

  }

  /* Visits the char literal expression rule
     Returns a CharLiterExprNode with the corresponding line number and character position
     for semantic error messages and the character value */
  @Override
  public ASTNode visitCharLiterExpr(CharLiterExprContext ctx) {

    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    /* If the given character is an escaped character strips off the escape symbol
       and returns a new CharLiterExprNode */
    switch (ctx.CHAR_LITER().getText()) {
      case "'\\0'":
        return new CharLiterExprNode(line, charPositionInLine, '\0');
      case "'\\b'":
        return new CharLiterExprNode(line, charPositionInLine, '\b');
      case "'\\t'":
        return new CharLiterExprNode(line, charPositionInLine, '\t');
      case "'\\n'":
        return new CharLiterExprNode(line, charPositionInLine, '\n');
      case "'\\f'":
        return new CharLiterExprNode(line, charPositionInLine, '\f');
      case "'\\r'":
        return new CharLiterExprNode(line, charPositionInLine, '\r');
      case "'\\\"'":
        return new CharLiterExprNode(line, charPositionInLine, '\"');
      case "'\\''":
        return new CharLiterExprNode(line, charPositionInLine, '\'');
      case "'\\\\'":
        return new CharLiterExprNode(line, charPositionInLine, '\\');
    }

    return new CharLiterExprNode(line, charPositionInLine, ctx.CHAR_LITER().getText().charAt(1));
  }

  /* Visits the string literal expression rule
     Returns a StringLiterExprNode with the corresponding line number and character position
     for semantic error messages and the string value */
  @Override
  public ASTNode visitStringLiterExpr(StringLiterExprContext ctx) {

    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new StringLiterExprNode(line, charPositionInLine, ctx.STR_LITER().getText());
  }

  /* Visits the int literal expression rule
     Calls visitInt_liter and returns a IntLiterExprNode */
  @Override
  public ASTNode visitIntLiterExpr(IntLiterExprContext ctx) {
    return visitInt_liter(ctx.int_liter());
  }

  /* Visits the pair literal expression rule
     Calls visitPair_liter and returns a PairLiterExprNode */
  @Override
  public ASTNode visitPairLiterExpr(PairLiterExprContext ctx) {
    return visitPair_liter(ctx.pair_liter());
  }

  /* Visits the identifier expression rule
     Returns an IdentifierNode with the corresponding line number and character position
     for semantic error messages and the ID converted to a string */
  @Override
  public ASTNode visitIdentExpr(IdentExprContext ctx) {

    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new IdentifierNode(line, charPositionInLine, ctx.IDENT().getText());
  }

  /* Visits the unary operator expression rule
     Returns a UnaryOpExprNode with the corresponding line number and character position
     for semantic error messages, an operand expression ExpressionNode and an operator */
  @Override
  public ASTNode visitUnaryExpr(UnaryExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    ExpressionNode operand = (ExpressionNode) visit(ctx.expr());

    /* returns the operator enum based on the input string */
    UnOp operator = Operator.UnOp.valueOfLabel(ctx.op.getText());

    /* if the given operator doesn't correspond to any known throws an exception */
    if (operator == null) {
      throw new IllegalArgumentException("Invalid Unary Operator");
    }

    return new UnaryOpExprNode(line, charPositionInLine, operand, operator);
  }

  /* Visits the bracket expression rule
     Returns a ParenthesisExprNode with the corresponding line number and character position
     for semantic error messages and the inner expression ExpressionNode */
  @Override
  public ASTNode visitBracketExpr(BracketExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());

    return new ParenthesisExprNode(line, charPositionInLine, expressionNode);
  }

  /* STATEMENT NODES */

  /* Visits the skip statement rule
     Returns a SkipStatementNode */
  @Override
  public ASTNode visitSkipStat(SkipStatContext ctx) {
    return new SkipStatementNode();
  }

  /* Visits the declaration statement rule
     Returns a DeclarationStatementNode with a visited TypeNode
     corresponding to the type of the declared variable
     an IdentifierNode the variable's ID and a visited AssignRHSNode
     corresponding to the value being assigned */
  @Override
  public ASTNode visitDeclStat(DeclStatContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    TypeNode type = (TypeNode) visit(ctx.type());

    IdentifierNode identifier = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT().getText());
    AssignRHSNode rhs = (AssignRHSNode) visit(ctx.assign_rhs());

    return new DeclarationStatementNode(type, identifier, rhs);
  }

  /* Visits the assignment statement rule
     Returns a AssignVarNode with a visited AssignLHSNode corresponding to the value on the LHS
     and a visited AssignRHSNode corresponding to the value on the RHS */
  @Override
  public ASTNode visitAssignStat(AssignStatContext ctx) {
    AssignLHSNode lhs = (AssignLHSNode) visit(ctx.assign_lhs());
    AssignRHSNode rhs = (AssignRHSNode) visit(ctx.assign_rhs());
    return new AssignVarNode(lhs, rhs);
  }

  /* Visits the read statement rule
     Returns a ReadStatementNode with a visited AssignLHSNode representing the assigned value */
  @Override
  public ASTNode visitReadStat(ReadStatContext ctx) {
    AssignLHSNode lhs = (AssignLHSNode) visit(ctx.assign_lhs());
    return new ReadStatementNode(lhs);
  }

  /* Visits the free statement rule
     Returns a FreeStatementNode with a visited ExpressionNode
     representing the expression to free */
  @Override
  public ASTNode visitFreeStat(FreeStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new FreeStatementNode(expr);
  }

  /* Visits the return statement rule
     Returns a ReturnStatementNode with a visited ExpressionNode
     representing the expression to return */
  @Override
  public ASTNode visitReturnStat(ReturnStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new ReturnStatementNode(expr);
  }

  /* Visits the exit statement rule
     Returns a ExitStatementNode with a visited ExpressionNode
     representing the expression with which to exit */
  @Override
  public ASTNode visitExitStat(ExitStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new ExitStatementNode(expr);
  }

  /* Visits the print statement rule
     Returns a PrintStatementNode with a visited ExpressionNode
     representing the expression to print */
  @Override
  public ASTNode visitPrintStat(PrintStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new PrintStatementNode(expr);
  }

  /* Visits the print line statement rule
     Returns a PrintLineStatementNode with a visited ExpressionNode
     representing the expression to print */
  @Override
  public ASTNode visitPrintlnStat(PrintlnStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new PrintLineStatementNode(expr);
  }

  /* Visits the if statement rule
     Returns an IfStatementNode with a visited condition ExpressionNode,
     a visited then statement StatementNode and a visited else statement StatementNode */
  @Override
  public ASTNode visitIfStat(IfStatContext ctx) {
    ExpressionNode condition = (ExpressionNode) visit(ctx.expr());
    StatementNode thenStatement = (StatementNode) visit(ctx.stat(0));
    StatementNode elseStatement = (StatementNode) visit(ctx.stat(1));
    return new IfStatementNode(condition, thenStatement, elseStatement);
  }

  /* Visits the while statement rule
     Returns a WhileStatementNode with a visited condition ExpressionNode
     and a visited body statement StatementNode */
  @Override
  public ASTNode visitWhileStat(WhileStatContext ctx) {
    ExpressionNode condition = (ExpressionNode) visit(ctx.expr());
    StatementNode statement = (StatementNode) visit(ctx.stat());
    return new WhileStatementNode(condition, statement);
  }

  /* Visits the scope statement rule
     Returns a NewScopeStatementNode with a visited body statement StatementNode */
  @Override
  public ASTNode visitScopeStat(ScopeStatContext ctx) {
    StatementNode statement = (StatementNode) visit(ctx.stat());
    return new NewScopeStatementNode(statement);
  }

  /* Visits the list of statements statement rule
     Returns a StatementsListNode with list containing
     visited left and right statement StatementNode */
  @Override
  public ASTNode visitStatsListStat(StatsListStatContext ctx) {
    List<StatementNode> statements = new ArrayList<>();
    statements.add((StatementNode) visit(ctx.stat(0)));
    statements.add((StatementNode) visit(ctx.stat(1)));
    return new StatementsListNode(statements);
  }

  /* TYPE AND LITERAL NODES */

  /* Visits the recursive base type rule
     Calls visitBase_type and returns a BaseTypeNode */
  @Override
  public ASTNode visitBaseType(BaseTypeContext ctx) {
    return visitBase_type(ctx.base_type());
  }

  /* Visits the array type rule
     Returns an ArrayTypeNode with a visited TypeNode
     corresponding to the type of the array's elements */
  @Override
  public ASTNode visitArrayType(ArrayTypeContext ctx) {
    return new ArrayTypeNode((TypeNode) visit(ctx.type()));
  }

  /* Visits the pair type rule
     Returns a PairTypeNode with a visited fst and snd elements TypeNodes */
  @Override
  public ASTNode visitPairType(PairTypeContext ctx) {
    TypeNode fst = (TypeNode) visit(ctx.pair_elem_type(0));
    TypeNode snd = (TypeNode) visit(ctx.pair_elem_type(1));

    return new PairTypeNode(fst, snd);
  }

  /* Visits the base type rule
     Returns a BaseTypeNode with the corresponding type */
  @Override
  public ASTNode visitBase_type(Base_typeContext ctx) {
    Type baseType = Type.valueOf(ctx.getText().toUpperCase());
    return new BaseTypeNode(baseType);
  }

  /* Visits the base pair element type rule
     Calls visitBase_type and returns a BaseTypeNode */
  @Override
  public ASTNode visitPairElemTypeBase(PairElemTypeBaseContext ctx) {
    return visitBase_type(ctx.base_type());
  }

  /* Visits the array pair element type rule
     Returns an ArrayTypeNode with the corresponding type */
  @Override
  public ASTNode visitPairElemTypeArray(PairElemTypeArrayContext ctx) {
    TypeNode type = (TypeNode) visit(ctx.type());
    return new ArrayTypeNode(type);
  }

  /* Visits the inner pair element type rule
     Returns an PairTypeNode with the elements initialized to null */
  @Override
  public ASTNode visitPairElemTypePair(PairElemTypePairContext ctx) {
    return new PairTypeNode(null, null);
  }

  /* Visits the pair literal rule
     Returns an PairLiterExprNode with the corresponding line number and character position
     for semantic error messages */
  @Override
  public ASTNode visitPair_liter(Pair_literContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new PairLiterExprNode(line, charPositionInLine);
  }

  /* Visits the int literal rule
     Returns an IntLiterExprNode with the corresponding line number and character position
     for semantic error messages and the integer representation of the given value */
  @Override
  public ASTNode visitInt_liter(Int_literContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new IntLiterExprNode(line, charPositionInLine,
        Integer.parseInt(ctx.getText()));
  }
}
