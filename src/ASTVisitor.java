import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.assignment.*;
import AbstractSyntaxTree.expression.*;
import AbstractSyntaxTree.statement.*;
import AbstractSyntaxTree.type.*;
import antlr.WACCParser.*;
import antlr.WACCParserBaseVisitor;

import java.util.ArrayList;
import java.util.List;

public class ASTVisitor extends WACCParserBaseVisitor<ASTNode> {

  /* PROGRAM NODE */
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
  @Override
  public ASTNode visitFunc(FuncContext ctx) {
    TypeNode type = (TypeNode) visit(ctx.type());
    IdentifierNode identifier = new IdentifierNode(ctx.IDENT().getText());
    StatementNode body = (StatementNode) visit(ctx.stat());
    ParamListNode params = new ParamListNode(new ArrayList<>(), new ArrayList<>());

    if(ctx.param_list() != null) {
      params = (ParamListNode) visit(ctx.param_list());
    }
    return new FunctionNode(type, identifier, params, body);
  }

  @Override
  public ASTNode visitParam_list(Param_listContext ctx) {
    List<IdentifierNode> names = new ArrayList<>();
    List<TypeNode> types = new ArrayList<>();

    if(ctx.IDENT() != null) {
      for(int i = 0; i < ctx.IDENT().size(); i++) {
        names.add(new IdentifierNode(ctx.IDENT(i).getText()));
        types.add((TypeNode) visit(ctx.type(i)));
      }
    }

    return super.visitParam_list(ctx);
  }

  /* RHS ASSIGNMENT NODES */
  @Override
  public ASTNode visitExprRHS(ExprRHSContext ctx) {
    return visit(ctx.expr());
  }

  @Override
  public ASTNode visitArrayLiterRHS(ArrayLiterRHSContext ctx) {
    List<ExpressionNode> expressionNodes = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      expressionNodes.add((ExpressionNode) visit(e));
    }
    return new ArrayLiterNode(expressionNodes);
  }

  @Override
  public ASTNode visitNewPairRHS(NewPairRHSContext ctx) {
    ExpressionNode leftExpr = (ExpressionNode) visit(ctx.expr(0));
    ExpressionNode rightExpr = (ExpressionNode) visit(ctx.expr(1));
    return new NewPairNode(leftExpr, rightExpr);
  }

  // figure out what happens here
  @Override
  public ASTNode visitPairElemRHS(PairElemRHSContext ctx) {
    return visit(ctx.pair_elem());
  }

  @Override
  public ASTNode visitFstPairExpr(FstPairExprContext ctx) {
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());
    return new PairElemNode(0, expressionNode);
  }

  @Override
  public ASTNode visitSndPairExpr(SndPairExprContext ctx) {
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());
    return new PairElemNode(1, expressionNode);
  }

  @Override
  public ASTNode visitFuncCallRHS(FuncCallRHSContext ctx) {

    IdentifierNode identifierNode = new IdentifierNode(ctx.IDENT().getText());
    List<ExpressionNode> arguments = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      arguments.add((ExpressionNode) visit(e));
    }
    return new FuncCallNode(identifierNode, arguments);
  }

  /* LHS ASSIGNMENT NODES */
  @Override
  public ASTNode visitIdentLHS(IdentLHSContext ctx) {
    return new IdentifierNode(ctx.IDENT().getText());
  }

  @Override
  public ASTNode visitArrayElemLHS(ArrayElemLHSContext ctx) {
    return visit(ctx.array_elem());
  }

  @Override
  public ASTNode visitArray_elem(Array_elemContext ctx) {
    IdentifierNode identifierNode = new IdentifierNode(ctx.IDENT().getText());
    List<ExpressionNode> expressions = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      expressions.add((ExpressionNode) visit(e));
    }
    return new ArrayElemNode(identifierNode, expressions);
  }

  @Override
  public ASTNode visitPairElemLHS(PairElemLHSContext ctx) {
    return visit(ctx.pair_elem());
  }

  /* EXPRESSION NODES */
  @Override
  public ASTNode visitArrayElemExpr(ArrayElemExprContext ctx) {
    return visit(ctx.array_elem());
  }

  // TODO: add a operator to class
  @Override
  public ASTNode visitUnaryExpr(UnaryExprContext ctx) {
    ExpressionNode operand = (ExpressionNode) visit(ctx.expr());
    return new UnaryOpExprNode(operand);
  }

  @Override
  public ASTNode visitBinaryExpr(BinaryExprContext ctx) {
    ExpressionNode leftExpr = (ExpressionNode) visit(ctx.expr(0));
    ExpressionNode rightExpr = (ExpressionNode) visit(ctx.expr(1));
    return new BinaryOpExprNode(leftExpr, rightExpr);
  }

  @Override
  public ASTNode visitBoolLiterExpr(BoolLiterExprContext ctx) {
    return new BoolLiterExprNode(Boolean.parseBoolean(ctx.BOOL_LITER().getText()));
  }

  @Override
  public ASTNode visitCharLiterExpr(CharLiterExprContext ctx) {
    switch (ctx.CHAR_LITER().getText()) {
      case "'\\0'":
        return new CharLiterExprNode('\0');
      case "'\\b'":
        return new CharLiterExprNode('\b');
      case "'\\t'":
        return new CharLiterExprNode('\t');
      case "'\\n'":
        return new CharLiterExprNode('\n');
      case "'\\f'":
        return new CharLiterExprNode('\f');
      case "'\\r'":
        return new CharLiterExprNode('\r');
      case "'\\\"'":
        return new CharLiterExprNode('\"');
      case "'\\''":
        return new CharLiterExprNode('\'');
      case "'\\\\'":
        return new CharLiterExprNode('\\');
    }

    return new CharLiterExprNode(ctx.CHAR_LITER().getText().charAt(1));
  }

  @Override
  public ASTNode visitStringLiterExpr(StringLiterExprContext ctx) {
    return new StringLiterExprNode(ctx.STR_LITER().getText());
  }

  @Override
  public ASTNode visitIntLiterExpr(IntLiterExprContext ctx) {
    return new IntLiterExprNode(Integer.parseInt(ctx.int_liter().getText()));
  }

  @Override
  public ASTNode visitPairLiterExpr(PairLiterExprContext ctx) {
    return new PairLiterExprNode();
  }

  @Override
  public ASTNode visitIdentExpr(IdentExprContext ctx) {
    return new IdentifierNode(ctx.IDENT().getText());
  }

  @Override
  public ASTNode visitBracketExpr(BracketExprContext ctx) {
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());
    return new ParenthesisExprNode(expressionNode);
  }

  /* STATEMENT NODES */
  //TODO: toString method of node to return "skip" or store skip in a field??
  @Override
  public ASTNode visitSkipStat(SkipStatContext ctx) {
    return new SkipStatementNode();
  }

  @Override
  public ASTNode visitDeclStat(DeclStatContext ctx) {
    TypeNode type = (TypeNode) visit(ctx.type());
    IdentifierNode identifier = new IdentifierNode(ctx.IDENT().getText());
    AssignRHSNode rhs = (AssignRHSNode) visit(ctx.assign_rhs());
    return new DeclarationStatementNode(type, identifier, rhs);
  }

  @Override
  public ASTNode visitAssignStat(AssignStatContext ctx) {
    AssignRHSNode rhs = (AssignRHSNode) visit(ctx.assign_rhs());
    AssignLHSNode lhs = (AssignLHSNode) visit(ctx.assign_lhs());
    return new AssignVarNode(lhs, rhs);
  }

  @Override
  public ASTNode visitReadStat(ReadStatContext ctx) {
    AssignLHSNode lhs = (AssignLHSNode) visit(ctx.assign_lhs());
    return new ReadStatementNode(lhs);
  }

  @Override
  public ASTNode visitFreeStat(FreeStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new FreeStatementNode(expr);
  }

  @Override
  public ASTNode visitReturnStat(ReturnStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new ReturnStatementNode(expr);
  }

  @Override
  public ASTNode visitExitStat(ExitStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new ExitStatementNode(expr);
  }

  @Override
  public ASTNode visitPrintStat(PrintStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new PrintStatementNode(expr);
  }

  @Override
  public ASTNode visitPrintlnStat(PrintlnStatContext ctx) {
    ExpressionNode expr = (ExpressionNode) visit(ctx.expr());
    return new PrintLineStatementNode(expr);
  }

  @Override
  public ASTNode visitIfStat(IfStatContext ctx) {
    ExpressionNode condition = (ExpressionNode) visit(ctx.expr());
    StatementNode thenStatement = (StatementNode) visit(ctx.stat(0));
    StatementNode elseStatement = (StatementNode) visit(ctx.stat(1));
    return new IfStatementNode(condition, thenStatement, elseStatement);
  }

  @Override
  public ASTNode visitWhileStat(WhileStatContext ctx) {
    ExpressionNode condition = (ExpressionNode) visit(ctx.expr());
    StatementNode statement = (StatementNode) visit(ctx.stat());
    return new WhileStatementNode(condition, statement);
  }

  @Override
  public ASTNode visitScopeStat(ScopeStatContext ctx) {
    StatementNode statement = (StatementNode) visit(ctx.stat());
    return new NewScopeStatementNode(statement);
  }

  //TODO: left-recursion? stat1 = list of statements, what then? unfold also stat2 after?
  // check in testing
  @Override
  public ASTNode visitStatsListStat(StatsListStatContext ctx) {
    List<StatementNode> statements = new ArrayList<>();
    StatementNode stat1 = (StatementNode) visit(ctx.stat(0)); //list
    StatementNode stat2 = (StatementNode) visit(ctx.stat(1));
    statements.add(stat1);
    statements.add(stat2);
    if (stat1 instanceof StatementsListNode) {
      //TODO
    }
    //TODO: for stat2 what?
    return new StatementsListNode(statements);
  }

  /* TYPE NODES */
  //TODO
  @Override
  public ASTNode visitBaseType(BaseTypeContext ctx) {
    return new BaseTypeNode(ctx.getText());
  }

  @Override
  public ASTNode visitArrayType(ArrayTypeContext ctx) {
    return new ArrayTypeNode((TypeNode) visit(ctx.type()));
  }

  @Override
  public ASTNode visitPairType(PairTypeContext ctx) {
    TypeNode fst = (TypeNode) visit(ctx.pair_elem_type(0));
    TypeNode snd = (TypeNode) visit(ctx.pair_elem_type(1));
    return new PairTypeNode(fst, snd);
  }

  public String hello() {
    return "hello";
  }

}