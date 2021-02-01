package ic.doc.semantic_analysis;

public abstract class Identifier {

    // ADD REFERENCE TO AST NODE !!!

    // pass in reference to ast node
    public Identifier(/* ASTNode node */) {
        // assign ast node to this.ast_node
    }

    //getter for the ast node
    public abstract String toString();
}
