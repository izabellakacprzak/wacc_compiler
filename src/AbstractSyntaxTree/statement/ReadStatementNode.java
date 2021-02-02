public class ReadStatementNode extends StatementNode {
    private final AssingLHSNode lhs;

    public ReadStatementNode(AssingLHSNode lhs){
      this.lhs = lhs;
    }
}