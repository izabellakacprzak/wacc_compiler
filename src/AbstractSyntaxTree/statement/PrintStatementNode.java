public class PrintStatementNode extends StatementNode {
  private final ExpressionNode expression;

  public PrintLineStatementNode(ExpressionNode expression){
    this.expression = expression;
  }
}