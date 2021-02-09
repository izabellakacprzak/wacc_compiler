package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.SymbolTable;
import org.antlr.v4.semantics.SymbolChecks;

public interface ExpressionNode extends AssignRHSNode {
    DataTypeId getType(SymbolTable symbolTable);
}