package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
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

    /* Check if any expression's type can be resolved */
    DataTypeId matchType = null;
    for (ExpressionNode expr : expressions) {
      matchType = expr.getType(symbolTable);

      if (matchType != null) {
        break;
      }
    }

    /* If no elements of the array have a type because at least one is an implicit parameter,
     *   add this node to unchecked nodes to be checked later */
    if (matchType == null && firstCheck) {
      for (ExpressionNode expr : expressions) {
        boolean isUnsetParam = expr.isUnsetParamId(symbolTable);
        boolean isUnsetArrayParam = false;

        if (expr instanceof ArrayElemNode) {
          isUnsetArrayParam = expr.isUnsetParamArray(symbolTable);
        }

        if (isUnsetParam || isUnsetArrayParam) {
          uncheckedNodes.add(this);
          return;
        }
      }
    }

    /* If the type of the array cannot be inferred, set any not inferred
     *   parameters to a default type */
    if (matchType == null) {
      for (ExpressionNode expr : expressions) {

        boolean isUnsetParam = expr.isUnsetParamId(symbolTable);
        boolean isUnsetArrayParam = false;
        if (expr instanceof ArrayElemNode) {
          isUnsetArrayParam = expr.isUnsetParamArray(symbolTable);
        }

        if (isUnsetParam) {
          ParameterId currParam = expr.getParamId(symbolTable);
          currParam.setType(DEFAULT_TYPE);
          matchType = DEFAULT_TYPE;
        } else if (isUnsetArrayParam) {
          ParameterId currArrayParam = ((ArrayElemNode) expr)
              .getUnsetParameterIdArrayElem(symbolTable);
          currArrayParam.setBaseElemType(DEFAULT_TYPE);
          matchType = DEFAULT_TYPE;
        }
      }

      /* If none of the expressions are implicit parameters and have no type, the type cannot
       *   be inferred */
      if (matchType == null) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Could not resolve type of array assignment."));
        return;

      }
    }

    /* Check if the other elements' types can be resolved and match the first element's type */
    for (ExpressionNode currExpr : expressions) {
      DataTypeId currType = currExpr.getType(symbolTable);

      /* If the expression is an implicit parameter, the type can be inferred */
      boolean isUnsetParam = currExpr.isUnsetParamId(symbolTable);
      boolean isUnsetArrayParam = false;
      if (currExpr instanceof ArrayElemNode) {
        isUnsetArrayParam = ((ArrayElemNode) currExpr).isUnsetParameterIdArrayElem(symbolTable);
      }

      if (isUnsetParam) {
        ParameterId currParam = currExpr.getParamId(symbolTable);
        currParam.setType(matchType);
      } else if (isUnsetArrayParam) {
        ParameterId currArrayParam = ((ArrayElemNode) currExpr)
            .getUnsetParameterIdArrayElem(symbolTable);
        currArrayParam.setBaseElemType(matchType);
      }

      /* Check if the type of the expression matches the matchType */
      currType = currExpr.getType(symbolTable);

      if (currType == null) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Could not resolve element type(s) in array literal."
                + " Expected: " + matchType));
        break;
      }

      if (!(matchType.equals(currType))) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Incompatible element type(s) in array literal."
                + " Expected: " + matchType + " Actual: " + currType));
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

  /* Returns an ArrayLiter in the form: [expr1, expr2, ..., exprN] */
  @Override
  public String toString() {
    return expressions.toString();
  }
}