package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Operator.UnOp;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;

import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

public class UnaryOpExprNode extends ExpressionNode {

  private static final DataTypeId DEFAULT_TYPE = new BaseType(INT);
  /* operand:  ExpressionNode corresponding to the expression the operator was called with
   * operator: UnOp enum representing the operator corresponding to this node */
  private final ExpressionNode operand;
  private final UnOp operator;

  public UnaryOpExprNode(int line, int charPositionInLine, ExpressionNode operand, UnOp operator) {
    super(line, charPositionInLine);
    this.operand = operand;
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


    /* Check that the operand type can be resolved and matches with one of the
     * operator's expected argument types */
    List<DataTypeId> argTypes = operator.getArgTypes();
    DataTypeId opType = operand.getType(symbolTable);

    boolean isUnsetParam = operand.isUnsetParamId(symbolTable);
    ParameterId param = operand.getParamId(symbolTable);

    boolean isUnsetArrayParam = false;
    ParameterId arrayParam = null;
    ArrayElemNode arrayElem = null;


    if (operand instanceof ArrayElemNode) {
      arrayElem = (ArrayElemNode) operand;
      isUnsetArrayParam = arrayElem.isUnsetParameterIdArrayElem(symbolTable);
      arrayParam = arrayElem.getUnsetParameterIdArrayElem(symbolTable);
    }

    if (isUnsetParam || isUnsetArrayParam) {

      /* UnaryOps for [getArgsType.size() == 1]: -, !, ord, chr.
      * CASE 1: can deduce type from unary operator. */
      if (operator.getArgTypes().size() == 1) {
        DataTypeId type = operator.getArgTypes().get(0);

        if (isUnsetParam) {
          param.setType(type);
        } else if (isUnsetArrayParam) {
          arrayParam.setBaseElemType(type);
        }
      } else /*CASE 2: len on arrays. */ {
        if (firstCheck) {
       /* 1st AST traversal, halt semantic analysis on this node if cannot infer the type. */
          uncheckedNodes.add(this);
          return;
        } else {
        /* 2nd AST traversal, set INT as default type for the array. */
          if (isUnsetParam) {
            param.setType(new ArrayType(DEFAULT_TYPE));
          } else if (isUnsetArrayParam) {
            arrayElem.setArrayElemBaseType(symbolTable, new ArrayType(DEFAULT_TYPE));
          }
          firstCheck = true;
        }
      }
    }


    opType = operand.getType(symbolTable);

    /* Recursively call semanticAnalysis on operand node */
    operand.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);


    if (opType == null) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Could not resolve type for '" + operator.getLabel() + "' operand."
              + " Expected: " + listTypeToString(argTypes)));
      return;
    }

    /* Check that at least one of the operator's possible
     * argument types matches the operand's type */
    boolean argMatched = false;

    for (DataTypeId argType : argTypes) {
      if (opType.equals(argType)) {
        argMatched = true;
        break;
      }
    }

    if (!argTypes.isEmpty() && !argMatched) {
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Invalid type for '" + operator.getLabel() + "' operator."
              + " Expected: " + listTypeToString(argTypes) + " Actual: " + opType));

    } else if (argTypes.isEmpty() && !(opType instanceof ArrayType)) {
      /* No expected argument types in argTypes implies an ARRAY is expected */
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Incompatible type for '" + operator.getLabel() + "' operator."
              + " Expected: ARRAY Actual: " + opType));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitUnaryOpExprNode(internalState, operand, operator);
  }

  @Override
  public DataTypeId getType(SymbolTable symTable) {
    return operator.getReturnType();
  }

  /* Returns a UnaryOpExpr in the form: operator expr */
  @Override
  public String toString() {
    return operator.getLabel() + " " + operand;
  }
}