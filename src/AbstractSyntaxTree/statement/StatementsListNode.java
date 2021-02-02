public class StatementsListNode extends StatementNode {
    private final List<StatementNode> statements;

    public StatementsListNode(List<StatementNode> statements){
      this.statements = statements;
    }
}