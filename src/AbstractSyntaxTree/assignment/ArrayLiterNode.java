package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class ArrayLiterNode extends AssignRHSNode {

  /* expressions:  List of ExpressionNodes corresponding to each element of the ARRAY literal */
  private final List<ExpressionNode> expressions;

  public ArrayLiterNode(int line, int charPositionInLine, List<ExpressionNode> expressions) {
    super(line, charPositionInLine);
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* If there are no expressions, then ARRAY literal is empty */
    if (expressions.isEmpty()) {
      return;
    }

    /* Check if the first expression's type can be resolved */
    ExpressionNode fstExpr = expressions.get(0);
    DataTypeId fstType = fstExpr.getType(symbolTable);

    if (fstType == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Could not resolve type of array assignment."));
      return;
    }

    fstExpr.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check if the other elements' types can be resolved and match the first element's type */
    for (int i = 1; i < expressions.size(); i++) {
      ExpressionNode currExpr = expressions.get(i);
      DataTypeId currType = currExpr.getType(symbolTable);

      if (currType == null) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Could not resolve element type(s) in array literal."
                + " Expected: " + fstType));
        break;
      }

      if (!(fstType.equals(currType))) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Incompatible element type(s) in array literal."
                + " Expected: " + fstType + " Actual: " + currType));
        break;
      }

      /* Recursively call semanticAnalysis on each expression node */
      currExpr.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitArrayLiterNode(internalState, expressions, this);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    if (expressions.size() == 0) {
      return new ArrayType(null);
    } else {
      return new ArrayType(expressions.get(0).getType(symbolTable));
    }
  }


  public void setParamTypes(DataTypeId type, SymbolTable symbolTable) {
    for (ExpressionNode expr : expressions) {
      if (expr.isUnsetParamId(symbolTable)) {
        ParameterId param = expr.getParamId(symbolTable);
        param.setType(type);
      }
    }
  }

  /* Returns an ArrayLiter in the form: [expr1, expr2, ..., exprN] */
  @Override
  public String toString() {
    return expressions.toString();
  }
}