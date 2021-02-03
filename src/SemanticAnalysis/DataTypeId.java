package SemanticAnalysis;

import AbstractSyntaxTree.ASTNode;

public abstract class DataTypeId extends Identifier {

    // pass in reference to ast node
    public DataTypeId(ASTNode node) {
        super(node);
    }

}
