package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class BinaryOpExprNode extends ExpressionNode {

  /* left:     ExpressionNode corresponding to the left expression the operator was called with
   * right:    ExpressionNode corresponding to the right expression the operator was called with
   * operator: BinOp enum representing the operator corresponding to this node */
  private final ExpressionNode left;
  private final ExpressionNode right;
  private final BinOp operator;

  public BinaryOpExprNode(int line, int charPositionInLine, ExpressionNode left,
                          ExpressionNode right,
                          BinOp operator) {
    super(line, charPositionInLine);
    this.left = left;
    this.right = right;
    this.operator = operator;
  }

  /* Returns the toString of a list without the square brackets "[]"
   * surrounding the elements */
  private String listTypeToString(List<DataTypeId> list) {
    StringBuilder argsStr = new StringBuilder().append(list);
    argsStr.deleteCharAt(argsStr.length() - 1).deleteCharAt(0);

    return argsStr.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* Check that the left assignment type and the right assignment type
     * can be resolved and match one of the operator's expected argument types */
    List<DataTypeId> argTypes = operator.getArgTypes();
    DataTypeId lhsType = left.getType(symbolTable);
    DataTypeId rhsType = right.getType(symbolTable);

    /* Check if either the left or right expressions need to have their types inferred */
    /* Set all needed booleans and variables for checking unset parameters */
    boolean isUnsetParamLeft = left.isUnsetParamId(symbolTable);
    boolean isUnsetParamRight = right.isUnsetParamId(symbolTable);
    ParameterId leftParam = left.getParamId(symbolTable);
    ParameterId rightParam = right.getParamId(symbolTable);

    boolean isUnsetArrayParamLeft = false;
    boolean isUnsetArrayParamRight = false;
    ParameterId leftArrayParam = null;
    ParameterId rightArrayParam = null;
    ArrayElemNode leftArrayElem = null;
    ArrayElemNode rightArrayElem = null;

    if (left instanceof ArrayElemNode) {
      leftArrayElem = (ArrayElemNode) left;
      isUnsetArrayParamLeft = leftArrayElem.isUnsetParameterIdArrayElem(symbolTable);
      leftArrayParam = leftArrayElem.getUnsetParameterIdArrayElem(symbolTable);
    }

    if (right instanceof ArrayElemNode) {
      rightArrayElem = (ArrayElemNode) right;
      isUnsetArrayParamRight = rightArrayElem.isUnsetParameterIdArrayElem(symbolTable);
      rightArrayParam = rightArrayElem.getUnsetParameterIdArrayElem(symbolTable);
    }

    /* If both operands are unset type parameters. */
    if ((isUnsetParamLeft || isUnsetArrayParamLeft)
            && (isUnsetParamRight || isUnsetArrayParamRight)) {

      /* CASE 1: the operands' types can be deduced directly from the operator. */
      if (argTypes.size() == 1) {
        if (isUnsetParamLeft) {                   //it is an unset identifier parameter
          leftParam.setType(argTypes.get(0));

        } else if (isUnsetArrayParamLeft) {       //it is unset array parameter
          leftArrayElem.setArrayElemBaseType(symbolTable, argTypes.get(0));
        }

        if (isUnsetParamRight) {                  //it is an unset identifier parameter
          rightParam.setType(argTypes.get(0));

        } else if (isUnsetArrayParamRight) {      //it is unset array parameter
          rightArrayElem.setArrayElemBaseType(symbolTable, argTypes.get(0));
        }

        /* CASE 2: the operands' types cannot be deduced directly from the operator
         * - if it is the first AST traversal, add left and right to each other's
         *     matchingParams lists and add the expected types from the operator */
      } else if (firstCheck) {
        ParameterId leftParamToAdd = isUnsetParamLeft ? leftParam : leftArrayParam;
        ParameterId rightParamToAdd = isUnsetParamRight ? rightParam : rightArrayParam;

        if (isUnsetParamLeft) {                   //it is an unset identifier parameter
          leftParam.addToMatchingParams(rightParamToAdd);
          leftParam.addToExpectedTypes(argTypes);

        } else if (isUnsetArrayParamLeft) {       //it is unset array parameter
          leftArrayElem.addToMatchingParamsArrayElem(symbolTable,
              rightParamToAdd);
          leftArrayElem.addToExpectedTypesArrayElem(symbolTable, argTypes);
        }

        if (isUnsetParamRight) {                  //it is an  unset identifier parameter
          rightParam.addToMatchingParams(leftParamToAdd);
          rightParam.addToExpectedTypes(argTypes);

        } else if (isUnsetArrayParamRight) {      //it is unset array parameter
          rightArrayElem.addToMatchingParamsArrayElem(symbolTable,
              leftParamToAdd);
          rightArrayElem.addToExpectedTypesArrayElem(symbolTable, argTypes);
        }

        boolean leftTypeIsNull = isUnsetParamLeft ? leftParam.getType() == null
            : leftArrayElem.getType(symbolTable) == null;
        boolean rightTypeIsNull = isUnsetParamRight ? rightParam.getType() == null
            : rightArrayElem.getType(symbolTable) == null;


        /* If the types of any still cannot be deduced, add this node to the unchecked Semantic
         *   Analysis nodes and stop the semantic analysis execution */

        if (leftTypeIsNull && rightTypeIsNull) {
          uncheckedNodes.add(this);
          return;
        }

        /* If this is the second traversal of the AST and we cannot infer anything from the
         *   operator, set the types to the DEFAULT_TYPE. */
      } else if (argTypes.isEmpty()) {
        if (isUnsetParamLeft) {                    //it is an unset identifier parameter
          leftParam.setType(DEFAULT_TYPE);
        } else if (isUnsetArrayParamLeft) {        //it is unset array parameter
          leftArrayElem.setArrayElemBaseType(symbolTable, DEFAULT_TYPE);
        }

        if (isUnsetParamRight) {                   //it is an unset identifier parameter
          rightParam.setType(DEFAULT_TYPE);
        } else if (isUnsetArrayParamRight) {       //it is unset array parameter
          rightArrayElem.setArrayElemBaseType(symbolTable, DEFAULT_TYPE);
        }

        firstCheck = true;

      } else {
        /* If this is the second traversal of the AST and we can infer multiple types
         *   from the operator take the first expected argument type of the operator */
        DataTypeId type = argTypes.get(0);

        if (isUnsetParamLeft) {                    //it is an unset identifier parameter
          leftParam.setType(type);
        } else if (isUnsetArrayParamLeft) {        //it is unset array parameter
          leftArrayElem.setArrayElemBaseType(symbolTable, type);
        }

        if (isUnsetParamRight) {                   //it is an unset identifier parameter
          rightParam.setType(type);
        } else if (isUnsetArrayParamRight) {       //it is unset array parameter
          rightArrayElem.setArrayElemBaseType(symbolTable, type);
        }

        firstCheck = true;
      }

    } else {
      /* If we can infer a type from at least one of the lhs or rhs, set it to the other
       *   in the case it is an implicit parameter. */

      if (isUnsetParamLeft) {
        leftParam.setType(rhsType);
      } else if (isUnsetArrayParamLeft) {
        leftArrayElem.setArrayElemBaseType(symbolTable, rhsType);
      }

      if (isUnsetParamRight) {
        rightParam.setType(lhsType);
      } else if (isUnsetArrayParamRight) {
        rightArrayElem.setArrayElemBaseType(symbolTable, lhsType);
      }
    }

    lhsType = left.getType(symbolTable);
    rhsType = right.getType(symbolTable);

    /* Recursively call semanticAnalysis on assignment nodes */
    left.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    right.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    if (lhsType == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Could not resolve type of LHS expression for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes)));
      return;
    }

    if (rhsType == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Could not resolve type of RHS expression for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes)));
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the LHS assignment's type */
    boolean argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (lhsType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    /* No expected argument types in argTypes implies any type is expected */
    if (!argTypes.isEmpty() && !argMatched) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Incompatible LHS type for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes) + " Actual: " + lhsType));
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the RHS assignment's type */
    argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (rhsType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    /* No expected argument types in argTypes implies any type is expected */
    if (!argTypes.isEmpty() && !argMatched) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Incompatible RHS type for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes) + " Actual: " + rhsType));
      return;
    }

    /* Check that the LHS and RHS assignment types match */
    if (!lhsType.equals(rhsType)) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "RHS type does not match LHS type for '" + operator.getLabel() + "' operator. "
              + "Expected: " + lhsType + " Actual: " + rhsType));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitBinaryOpExprNode(internalState, left, right, operator);
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return operator.getReturnType();
  }

  /* Returns a BinaryOpExpr in the form: left_expr operator right_expr */
  @Override
  public String toString() {
    return left + " " + operator.getLabel() + " " + right;
  }
}
