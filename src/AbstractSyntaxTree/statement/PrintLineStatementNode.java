public class PrintLineStatementNode extends StatementNode {
    private final ExpressionNode expression;

    public PrintLineStatementNode(ExpressionNode expression){
      this.expression = expression;
    }
}