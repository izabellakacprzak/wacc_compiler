package AbstractSyntaxTree.expression;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.Operator.BinOp;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;

import java.lang.reflect.Parameter;
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

  private static void matchTypes(SymbolTable symbolTable, ExpressionNode left, ExpressionNode right) {
    DataTypeId lhsType = left.getType(symbolTable);
    DataTypeId rhsType = right.getType(symbolTable);
    if (lhsType == null) {
      if (left instanceof IdentifierNode) {
        IdentifierNode leftIdentifierNode = (IdentifierNode) left;
        Identifier leftIdentifierId = symbolTable.lookupAll(leftIdentifierNode.getIdentifier());
        if (leftIdentifierId instanceof ParameterId) {
          ParameterId leftParam = (ParameterId) leftIdentifierId;
          if (rhsType != null) {
            leftParam.setType(rhsType);
          } else {
            if (right instanceof IdentifierNode) {
              IdentifierNode rightIdentifierNode = (IdentifierNode) right;
              Identifier rightIdentifierId = symbolTable.lookupAll(rightIdentifierNode.getIdentifier());
              if (rightIdentifierId instanceof ParameterId) {
                ParameterId rightParam = (ParameterId) rightIdentifierId;
                leftParam.addToMatchingTypeParams(rightIdentifierNode);
                rightParam.addToMatchingTypeParams(leftIdentifierNode);
              }
            }
          }
        }
      }
    }
  }

  /* Returns the toString of a list without the square brackets "[]"
   * surrounding the elements */
  private String listTypeToString(List<DataTypeId> list) {
    StringBuilder argsStr = new StringBuilder().append(list);
    argsStr.deleteCharAt(argsStr.length() - 1).deleteCharAt(0);

    return argsStr.toString();
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);


    /* Recursively call semanticAnalysis on assignment nodes */
    left.semanticAnalysis(symbolTable, errorMessages);
    right.semanticAnalysis(symbolTable, errorMessages);


    /* Check that the left assignment type and the right assignment type
     * can be resolved and match one of the operator's expected argument types */
    List<DataTypeId> argTypes = operator.getArgTypes();
    DataTypeId lhsType = left.getType(symbolTable);
    DataTypeId rhsType = right.getType(symbolTable);

    if (argTypes.size() == 1) {
      passOperatorType(symbolTable, argTypes, lhsType, left);
      passOperatorType(symbolTable, argTypes, rhsType, right);
    } else {
      matchTypes(symbolTable, left, right);
      matchTypes(symbolTable, right, left);
    }


    if (!isParameter(left, symbolTable) && lhsType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Could not resolve type of LHS expression for '" + operator.getLabel() + "' operator."
                            + " Expected: " + listTypeToString(argTypes));
      return;
    }

    if (!isParameter(right, symbolTable) && rhsType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Could not resolve type of RHS expression for '" + operator.getLabel() + "' operator."
                            + " Expected: " + listTypeToString(argTypes));
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the LHS assignment's type */
    boolean argMatched = false;

    if (isParameter(left, symbolTable)) {
      for (DataTypeId argType : argTypes) {
        ParameterId leftParam = getParameter(left, symbolTable);
        leftParam.addToExpectedTypes(argType);
      }
    } else {
      for (DataTypeId argType : argTypes) {
        if (lhsType.equals(argType)) {
          argMatched = true;
          break;
        }
      }
    }

    /* No expected argument types in argTypes implies any type is expected */
    if (!isParameter(left, symbolTable) && !argTypes.isEmpty() && !argMatched) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Incompatible LHS type for '" + operator.getLabel() + "' operator."
                            + " Expected: " + listTypeToString(argTypes) + " Actual: " + lhsType);
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the RHS assignment's type */
    argMatched = false;

    if (isParameter(right, symbolTable)) {
      for (DataTypeId argType : argTypes) {
        ParameterId rightParam = getParameter(right, symbolTable);
        rightParam.addToExpectedTypes(argType);
      }
    }
      for (DataTypeId argType : argTypes) {
        if (rhsType.equals(argType)) {
          argMatched = true;
          break;
        }
      }


    /* No expected argument types in argTypes implies any type is expected */
    if (!isParameter(right, symbolTable) && !argTypes.isEmpty() && !argMatched) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " Incompatible RHS type for '" + operator.getLabel() + "' operator."
                            + " Expected: " + listTypeToString(argTypes) + " Actual: " + rhsType);
      return;
    }

    /* Check that the LHS and RHS assignment types match */
    if (!isParameter(left, symbolTable) && !isParameter(right, symbolTable) && !lhsType.equals(rhsType)) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
                            + " RHS type does not match LHS type for '" + operator.getLabel() + "' operator. "
                            + "Expected: " + lhsType + " Actual: " + rhsType);
    }
  }

  private void passOperatorType(SymbolTable symbolTable, List<DataTypeId> argTypes, DataTypeId type, ExpressionNode node) {
    if (type == null) {
      if (node instanceof IdentifierNode) {
        IdentifierNode identifierNode = (IdentifierNode) node;
        Identifier identifierId = symbolTable.lookupAll(identifierNode.getIdentifier());
        if (identifierId instanceof ParameterId) {
          ParameterId param = (ParameterId) identifierId;
          param.setType(argTypes.get(1));
        }
      }
    }
  }

  private boolean isParameter(ExpressionNode node, SymbolTable symbolTable) {
    if (node instanceof IdentifierNode) {
      IdentifierNode identifierNode = (IdentifierNode) node;
      Identifier identifierId = symbolTable.lookupAll(identifierNode.getIdentifier());
      return (identifierId instanceof ParameterId);
    }
    return false;
  }

  private ParameterId getParameter(ExpressionNode node, SymbolTable symbolTable) {
    if (node instanceof IdentifierNode) {
      IdentifierNode identifierNode = (IdentifierNode) node;
      Identifier identifierId = symbolTable.lookupAll(identifierNode.getIdentifier());
      if (identifierId instanceof ParameterId) {
        return (ParameterId) identifierId;
      }
    }
    return null;
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
