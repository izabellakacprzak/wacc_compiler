import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.assignment.*;
import AbstractSyntaxTree.expression.*;
import AbstractSyntaxTree.statement.StatementNode;
import AbstractSyntaxTree.type.FunctionNode;
import antlr.WACCParser.*;
import antlr.WACCParserBaseVisitor;
import java.util.ArrayList;
import java.util.List;

public class ASTVisitor extends WACCParserBaseVisitor<ASTNode> {

  @Override
  public ASTNode visitProgram(ProgramContext ctx) {
    StatementNode statementNode = (StatementNode) visit(ctx.stat());
    List<FunctionNode> functionNodes = new ArrayList<>();
    for(FuncContext f : ctx.func()) {
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
    List<ExpressionNode> expressionNodes = new ArrayList<>();
    for(ExprContext e : ctx.expr()) {
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

    IdentifierNode identifierNode = new IdentifierNode(ctx.IDENT().toString());
    List<ExpressionNode> arguments = new ArrayList<>();
    for(ExprContext e : ctx.expr()) {
      arguments.add((ExpressionNode) visit(e));
    }
    return new FuncCallNode(identifierNode, arguments);
  }

  @Override
  public ASTNode visitIdentLHS(IdentLHSContext ctx) {
    return new IdentifierNode(ctx.IDENT().toString());
  }

  @Override
  public ASTNode visitArrayElemLHS(ArrayElemLHSContext ctx) {
    return visit(ctx.array_elem());
  }

  @Override
  public ASTNode visitArray_elem(Array_elemContext ctx) {
    IdentifierNode identifierNode = new IdentifierNode(ctx.IDENT().toString());
    List<ExpressionNode> expressions = new ArrayList<>();
    for(ExprContext e : ctx.expr()) {
      expressions.add((ExpressionNode) visit(e));
    }
    return new ArrayElemNode(identifierNode, expressions);
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
    ExpressionNode leftExpr = (ExpressionNode) visit(ctx.expr(0));
    ExpressionNode rightExpr = (ExpressionNode) visit(ctx.expr(1));
    return new BinaryOpExprNode(leftExpr, rightExpr);
  }

  @Override
  public ASTNode visitBoolLiterExpr(BoolLiterExprContext ctx) {
    return new BoolLiterExprNode(Boolean.parseBoolean(ctx.BOOL_LITER().toString()));
  }

  @Override
  public ASTNode visitCharLiterExpr(CharLiterExprContext ctx) {
    switch(ctx.CHAR_LITER().toString()) {
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

    return new CharLiterExprNode(ctx.CHAR_LITER().toString().charAt(1));
  }

  @Override
  public ASTNode visitStringLiterExpr(StringLiterExprContext ctx) {
    return new StringLiterExprNode(ctx.STR_LITER().toString());
  }

  @Override
  public ASTNode visitIntLiterExpr(IntLiterExprContext ctx) {
    return new IntLiterExprNode(Integer.parseInt(ctx.INT_LITER().toString()));
  }

  @Override
  public ASTNode visitPairLiterExpr(PairLiterExprContext ctx) {
    return new PairLiterExprNode();
  }

  @Override
  public ASTNode visitIdentExpr(IdentExprContext ctx) {
    return new IdentifierNode(ctx.IDENT().toString());
  }


  // TODO: add a operator to class
  @Override
  public ASTNode visitUnaryExpr(UnaryExprContext ctx) {
    ExpressionNode operand = (ExpressionNode) visit(ctx.expr());
    return new UnaryOpExprNode(operand);
  }

  @Override
  public ASTNode visitBracketExpr(BracketExprContext ctx) {
    ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr());
    return new ParenthesisExprNode(expressionNode);
  }
}
