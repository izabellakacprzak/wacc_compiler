import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.assignment.ArrayLiterNode;
import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import AbstractSyntaxTree.assignment.FuncCallNode;
import AbstractSyntaxTree.assignment.NewPairNode;
import AbstractSyntaxTree.assignment.PairElemNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.BinaryOpExprNode;
import AbstractSyntaxTree.expression.BoolLiterExprNode;
import AbstractSyntaxTree.expression.CharLiterExprNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import AbstractSyntaxTree.expression.IntLiterExprNode;
import AbstractSyntaxTree.expression.PairLiterExprNode;
import AbstractSyntaxTree.expression.ParenthesisExprNode;
import AbstractSyntaxTree.expression.StringLiterExprNode;
import AbstractSyntaxTree.expression.UnaryOpExprNode;
import AbstractSyntaxTree.statement.AssignVarNode;
import AbstractSyntaxTree.statement.DeclarationStatementNode;
import AbstractSyntaxTree.statement.ExitStatementNode;
import AbstractSyntaxTree.statement.FreeStatementNode;
import AbstractSyntaxTree.statement.IfStatementNode;
import AbstractSyntaxTree.statement.NewScopeStatementNode;
import AbstractSyntaxTree.statement.PrintLineStatementNode;
import AbstractSyntaxTree.statement.PrintStatementNode;
import AbstractSyntaxTree.statement.ReadStatementNode;
import AbstractSyntaxTree.statement.ReturnStatementNode;
import AbstractSyntaxTree.statement.SkipStatementNode;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.statement.StatementsListNode;
import AbstractSyntaxTree.statement.WhileStatementNode;
import AbstractSyntaxTree.type.ArrayTypeNode;
import AbstractSyntaxTree.type.BaseTypeNode;
import AbstractSyntaxTree.type.FunctionNode;
import AbstractSyntaxTree.type.PairTypeNode;
import AbstractSyntaxTree.type.TypeNode;
import SemanticAnalysis.DataTypes.BaseType.Type;
import SemanticAnalysis.Operator;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.Operator.UnOp;
import antlr.WACCParser.ArrayElemExprContext;
import antlr.WACCParser.ArrayElemLHSContext;
import antlr.WACCParser.ArrayLiterRHSContext;
import antlr.WACCParser.ArrayTypeContext;
import antlr.WACCParser.Array_elemContext;
import antlr.WACCParser.AssignStatContext;
import antlr.WACCParser.BaseTypeContext;
import antlr.WACCParser.BinaryExprContext;
import antlr.WACCParser.BoolLiterExprContext;
import antlr.WACCParser.BracketExprContext;
import antlr.WACCParser.CharLiterExprContext;
import antlr.WACCParser.DeclStatContext;
import antlr.WACCParser.ExitStatContext;
import antlr.WACCParser.ExprContext;
import antlr.WACCParser.ExprRHSContext;
import antlr.WACCParser.FreeStatContext;
import antlr.WACCParser.FstPairExprContext;
import antlr.WACCParser.FuncCallRHSContext;
import antlr.WACCParser.FuncContext;
import antlr.WACCParser.IdentExprContext;
import antlr.WACCParser.IdentLHSContext;
import antlr.WACCParser.IfStatContext;
import antlr.WACCParser.IntLiterExprContext;
import antlr.WACCParser.NewPairRHSContext;
import antlr.WACCParser.PairElemLHSContext;
import antlr.WACCParser.PairElemRHSContext;
import antlr.WACCParser.PairLiterExprContext;
import antlr.WACCParser.PairTypeContext;
import antlr.WACCParser.PrintStatContext;
import antlr.WACCParser.PrintlnStatContext;
import antlr.WACCParser.ProgramContext;
import antlr.WACCParser.ReadStatContext;
import antlr.WACCParser.ReturnStatContext;
import antlr.WACCParser.ScopeStatContext;
import antlr.WACCParser.SkipStatContext;
import antlr.WACCParser.SndPairExprContext;
import antlr.WACCParser.StatsListStatContext;
import antlr.WACCParser.StringLiterExprContext;
import antlr.WACCParser.UnaryExprContext;
import antlr.WACCParser.WhileStatContext;
import antlr.WACCParserBaseVisitor;
import java.util.ArrayList;
import java.util.List;

public class ASTVisitor extends WACCParserBaseVisitor<ASTNode> {

  @Override
  public ASTNode visitProgram(ProgramContext ctx) {
    StatementNode statementNode = (StatementNode) visit(ctx.stat());
    List<FunctionNode> functionNodes = new ArrayList<>();
    for (FuncContext f : ctx.func()) {
      functionNodes.add((FunctionNode) visit(f));
    }
    return new ProgramNode(statementNode, functionNodes);
  }

  @Override
  public ASTNode visitExprRHS(ExprRHSContext ctx) {
    return visit(ctx.expr());
  }

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

  @Override
  public ASTNode visitNewPairRHS(NewPairRHSContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode leftExpr = (ExpressionNode) visit(ctx.expr(0));
    ExpressionNode rightExpr = (ExpressionNode) visit(ctx.expr(1));

    return new NewPairNode(line, charPositionInLine, leftExpr, rightExpr);
  }

  // figure out what happens here
  @Override
  public ASTNode visitPairElemRHS(PairElemRHSContext ctx) {
    return visit(ctx.pair_elem());
  }

  @Override
  public ASTNode visitFstPairExpr(FstPairExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());

    return new PairElemNode(line, charPositionInLine, 0, expressionNode);
  }

  @Override
  public ASTNode visitSndPairExpr(SndPairExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());

    return new PairElemNode(line, charPositionInLine, 1, expressionNode);
  }

  @Override
  public ASTNode visitFuncCallRHS(FuncCallRHSContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    IdentifierNode identifierNode = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT().toString());
    List<ExpressionNode> arguments = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      arguments.add((ExpressionNode) visit(e));
    }
    return new FuncCallNode(line, charPositionInLine, identifierNode, arguments);
  }

  @Override
  public ASTNode visitIdentLHS(IdentLHSContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new IdentifierNode(line, charPositionInLine, ctx.IDENT().toString());
  }

  @Override
  public ASTNode visitArrayElemLHS(ArrayElemLHSContext ctx) {
    return visit(ctx.array_elem());
  }

  @Override
  public ASTNode visitArray_elem(Array_elemContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    IdentifierNode identifierNode = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT().toString());
    List<ExpressionNode> expressions = new ArrayList<>();
    for (ExprContext e : ctx.expr()) {
      expressions.add((ExpressionNode) visit(e));
    }
    return new ArrayElemNode(line, charPositionInLine, identifierNode, expressions);
  }

  @Override
  public ASTNode visitPairElemLHS(PairElemLHSContext ctx) {
    return visit(ctx.pair_elem());
  }

  @Override
  public ASTNode visitArrayElemExpr(ArrayElemExprContext ctx) {
    return visit(ctx.array_elem());
  }

  @Override
  public ASTNode visitBinaryExpr(BinaryExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    ExpressionNode leftExpr = (ExpressionNode) visit(ctx.expr(0));
    ExpressionNode rightExpr = (ExpressionNode) visit(ctx.expr(1));
    BinOp operator = Operator.BinOp.valueOfLabel(ctx.op.toString());

    if (operator == null) {
      throw new IllegalArgumentException("Invalid Binary Operator");
    }

    return new BinaryOpExprNode(line, charPositionInLine, leftExpr, rightExpr, operator);
  }

  @Override
  public ASTNode visitBoolLiterExpr(BoolLiterExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new BoolLiterExprNode(line, charPositionInLine,
        Boolean.parseBoolean(ctx.BOOL_LITER().toString()));
  }

  @Override
  public ASTNode visitCharLiterExpr(CharLiterExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    switch (ctx.CHAR_LITER().toString()) {
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

    return new CharLiterExprNode(line, charPositionInLine, ctx.CHAR_LITER().toString().charAt(1));
  }

  @Override
  public ASTNode visitStringLiterExpr(StringLiterExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new StringLiterExprNode(line, charPositionInLine, ctx.STR_LITER().toString());
  }

  @Override
  public ASTNode visitIntLiterExpr(IntLiterExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new IntLiterExprNode(line, charPositionInLine,
        Integer.parseInt(ctx.INT_LITER().toString()));
  }

  @Override
  public ASTNode visitPairLiterExpr(PairLiterExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new PairLiterExprNode(line, charPositionInLine);
  }

  @Override
  public ASTNode visitIdentExpr(IdentExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    return new IdentifierNode(line, charPositionInLine, ctx.IDENT().toString());
  }

  // TODO: add a operator to class
  @Override
  public ASTNode visitUnaryExpr(UnaryExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();

    ExpressionNode operand = (ExpressionNode) visit(ctx.expr());
    UnOp operator = Operator.UnOp.valueOfLabel(ctx.op.toString());

    if (operator == null) {
      throw new IllegalArgumentException("Invalid Unary Operator");
    }

    return new UnaryOpExprNode(line, charPositionInLine, operand, operator);
  }

  @Override
  public ASTNode visitBracketExpr(BracketExprContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());

    return new ParenthesisExprNode(line, charPositionInLine, expressionNode);
  }

  // TODO: toString method of node to return "skip" or store skip in a field??
  @Override
  public ASTNode visitSkipStat(SkipStatContext ctx) {
    return new SkipStatementNode();
  }

  @Override
  public ASTNode visitDeclStat(DeclStatContext ctx) {
    int line = ctx.getStart().getLine();
    int charPositionInLine = ctx.getStart().getCharPositionInLine();
    TypeNode type = (TypeNode) visit(ctx.type());
    IdentifierNode identifier = new IdentifierNode(line, charPositionInLine,
        ctx.IDENT().toString());
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

  // TODO: left-recursion? stat1 = list of statements, what then? unfold also stat2 after?
  // check in testing
  @Override
  public ASTNode visitStatsListStat(StatsListStatContext ctx) {
    List<StatementNode> statements = new ArrayList<>();
    StatementNode stat1 = (StatementNode) visit(ctx.stat(0)); // list
    StatementNode stat2 = (StatementNode) visit(ctx.stat(1));
    statements.add(stat1);
    statements.add(stat2);
    if (stat1 instanceof StatementsListNode) {
      // TODO
    }
    // TODO: for stat2 what?
    return new StatementsListNode(statements);
  }

  // TODO
  @Override
  public ASTNode visitBaseType(BaseTypeContext ctx) {
    Type baseType = Type.valueOf(ctx.base_type().toString().toUpperCase());

    return new BaseTypeNode(baseType, ctx.toString());
  }

  @Override
  public ASTNode visitArrayType(ArrayTypeContext ctx) {
    return new ArrayTypeNode((TypeNode) visit(ctx.type()));
  }

  @Override
  public ASTNode visitPairType(PairTypeContext ctx) {
    TypeNode fst = (TypeNode) visit(ctx.pair_type().pair_elem_type(0));
    TypeNode snd = (TypeNode) visit(ctx.pair_type().pair_elem_type(1));

    return new PairTypeNode(fst, snd);
  }

  public String hello() {
    return "hello";
  }
}
