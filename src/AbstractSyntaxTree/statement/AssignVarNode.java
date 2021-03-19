package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.AssignLHSNode;
import AbstractSyntaxTree.assignment.AssignRHSNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import AbstractSyntaxTree.expression.ArrayElemNode;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class AssignVarNode extends StatementNode {

  /* left:         AssignLHSNode corresponding to what being assigned to
   * right:        AssignRHSNode corresponding to the assignment for the AssignLHSNode */
  private final AssignLHSNode left;
  private final AssignRHSNode right;

  public AssignVarNode(AssignLHSNode left, AssignRHSNode right) {
    this.left = left;
    this.right = right;
  }

  /* Check whether the inability to resolve the assignment does not stem from an undeclared variable.
   * true if variable has been declared in the current scope. */
  public boolean varHasBeenDeclared(List<SemanticError> errorMessages, AssignLHSNode node) {
    if (errorMessages.isEmpty()) {
      return true;
    }

    String lastErrorMsg = errorMessages.get(errorMessages.size() - 1).getMessage();
    String line = Integer.toString(node.getLine());
    String charPos = Integer.toString(node.getCharPositionInLine());

    return (!lastErrorMsg.contains(line + ":" + charPos) || !lastErrorMsg.contains("Identifier"));
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);


    /* Get possible parameters for identifier nodes needed if an identifier corresponds to
     *   an unset parameter identifier */
    DataTypeId leftType = left.getType(symbolTable);
    DataTypeId rightType = getTypeOfOverloadFunc(symbolTable, errorMessages, leftType, right);
    boolean isUnsetParamLeft = left.isUnsetParamId(symbolTable);
    boolean isUnsetParamRight = right.isUnsetParamId(symbolTable);
    ParameterId leftParam = left.getParamId(symbolTable);
    ParameterId rightParam = right.getParamId(symbolTable);

    /* Get possible parameters for array nodes needed if an array element's identifier
     *   corresponds to an unset parameter identifier */
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

      if (firstCheck) {
        /* CASE 1: this is the first AST traversal and both left and right haven't been inferred */
        uncheckedNodes.add(this);

        /* Add each to each other's matching params list */
        ParameterId leftParamToAdd = isUnsetParamLeft ? leftParam : leftArrayParam;
        ParameterId rightParamToAdd = isUnsetParamRight ? rightParam : rightArrayParam;

        if (isUnsetParamLeft) {                   //it is an unset identifier parameter
          leftParam.addToMatchingParams(rightParamToAdd);
        } else if (isUnsetArrayParamLeft) {       //it is unset array parameter
          leftArrayElem.addToMatchingParamsArrayElem(symbolTable,
              rightParamToAdd);
        }

        if (isUnsetParamRight) {                  //it is an  unset identifier parameter
          rightParam.addToMatchingParams(leftParamToAdd);
        } else if (isUnsetArrayParamRight) {      //it is unset array parameter
          rightArrayElem.addToMatchingParamsArrayElem(symbolTable,
              leftParamToAdd);
        }

        return;

      } else {
        /*CASE 2: this is the second AST traversal and both left and right haven't been inferred */
        if (isUnsetParamLeft) {                   //it is an unset identifier parameter
          leftParam.setType(DEFAULT_TYPE);
        } else if (isUnsetArrayParamLeft) {       //it is unset array parameter
          leftArrayElem.setArrayElemBaseType(symbolTable, DEFAULT_TYPE);
        }

        if (isUnsetParamRight) {                  //it is an unset identifier parameter
          rightParam.setType(DEFAULT_TYPE);
        } else if (isUnsetArrayParamRight) {      //it is unset array parameter
          rightArrayElem.setArrayElemBaseType(symbolTable, DEFAULT_TYPE);
        }
        firstCheck = true;
      }
    } else {
      /* If we can infer a type from at least one of the lhs or rhs, set it to the other
       *   in case it is an implicit parameter. */

      if (isUnsetParamLeft) {
        leftParam.setType(rightType);
      } else if (isUnsetArrayParamLeft) {
        leftArrayElem.setArrayElemBaseType(symbolTable, rightType);
      }

      if (isUnsetParamRight) {
        rightParam.setType(leftType);
      } else if (isUnsetArrayParamRight) {
        rightArrayElem.setArrayElemBaseType(symbolTable, leftType);
      }
    }

    /* Recursively call semanticAnalysis on LHS node */
    left.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    right.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that the left assignment type and the right assignment type
     * can be resolved and match */
    leftType = left.getType(symbolTable);

    if (leftType == null) {
      if (varHasBeenDeclared(errorMessages, left)) {
        errorMessages.add(new SemanticError(left.getLine(), left.getCharPositionInLine(),
            "Could not resolve type of LHS assignment '" + left + "'."));
      }
      return;
    }

    rightType = getTypeOfOverloadFunc(symbolTable, errorMessages, leftType, right);

    if (rightType == null) {
      if (varHasBeenDeclared(errorMessages, right)) {
        errorMessages.add(new SemanticError(right.getLine(), right.getCharPositionInLine(),
            "Could not resolve type of RHS assignment'" + right + "'."));
      }
    } else if (!leftType.equals(rightType) && !stringToCharArray(leftType, rightType)) {
      errorMessages.add(new SemanticError(left.getLine(), left.getCharPositionInLine(),
          "RHS type does not match LHS type for assignment. "
              + " Expected: " + leftType + " Actual: " + rightType));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
        visitAssignVarNode(internalState, left, right, getCurrSymTable());
  }

}

