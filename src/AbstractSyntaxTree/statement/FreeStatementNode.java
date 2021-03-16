package AbstractSyntaxTree.statement;

import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class FreeStatementNode extends StatementNode {

  private static final DataTypeId DEFAULT_TYPE = new ArrayType(new BaseType(INT));

  /* expression:   ExpressionNode corresponding to the expression 'free' was called with */
  private final ExpressionNode expression;

  public FreeStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    DataTypeId exprType = expression.getType(symbolTable);

    boolean isUnsetParam = expression.isUnsetParamId(symbolTable);
    ParameterId param = expression.getParamId(symbolTable);

    boolean isUnsetArrayParam = false;
    ParameterId arrayParam = null;
    ArrayElemNode arrayElem = null;


    if (expression instanceof ArrayElemNode) {
      arrayElem = (ArrayElemNode) expression;
      isUnsetArrayParam = arrayElem.isUnsetParameterIdArrayElem(symbolTable);
      arrayParam = arrayElem.getUnsetParameterIdArrayElem(symbolTable);
    }


    if (isUnsetParam || isUnsetArrayParam) {

      if (firstCheck) {
        List<DataTypeId> expecteds = new ArrayList<>();
        expecteds.add(new ArrayType(null));
        expecteds.add(new PairType(null, null));

        if (isUnsetParam) {
          param.addToExpectedTypes(expecteds);
        } else if (isUnsetArrayParam) {
          arrayParam.addToExpectedTypes(expecteds);
        }

        boolean paramTypeIsStillNull = (isUnsetParam) ? param.getType() == null
                                           : arrayElem.getType(symbolTable)  == null;
        if (paramTypeIsStillNull) {
          uncheckedNodes.add(this);
          return;
        }

      } else {
        if (isUnsetParam) {
          param.setType(DEFAULT_TYPE);
        } else if (isUnsetArrayParam) {
          arrayElem.setArrayElemBaseType(symbolTable, DEFAULT_TYPE);
        }

        firstCheck = true;
      }
    }


    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that the type of assignment is an ARRAY or a PAIR */
     exprType = expression.getType(symbolTable);

    if (exprType == null) {
      errorMessages.add(new SemanticError(expression.getLine(), expression.getCharPositionInLine(),
          "Could not resolve type for '" + expression + "'."
              + " Expected: ARRAY, PAIR"));

    } else if (!exprType.equals(new PairType(null, null)) &&
        !exprType.equals(new ArrayType(null))) {
      errorMessages.add(new SemanticError(expression.getLine(), expression.getCharPositionInLine(),
          " Incompatible type for 'free' statement." +
              " Expected: ARRAY, PAIR Actual: " + exprType));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitFreeStatementNode(internalState, expression);
  }

}
