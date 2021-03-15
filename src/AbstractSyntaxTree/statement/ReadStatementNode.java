package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.assignment.ArrayLiterNode;
import AbstractSyntaxTree.assignment.AssignLHSNode;
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

public class ReadStatementNode extends StatementNode {

  private static final DataTypeId DEFAULT_TYPE = new BaseType(BaseType.Type.INT);
  /* assignment:   AssignLHSNode corresponding to the IdentifierNode, ArrayElemNode
   *                 or PairElemNode 'read' was called with */
  private final AssignLHSNode assignment;

  public ReadStatementNode(AssignLHSNode assignment) {
    this.assignment = assignment;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    DataTypeId assignmentType = assignment.getType(symbolTable);
    boolean isUnsetParam = assignment.isUnsetParamId(symbolTable);
    ParameterId param = assignment.getParamId(symbolTable);

    boolean isUnsetArrayParam = false;
    ParameterId arrayParam = null;
    ArrayElemNode arrayElem = null;


    if (assignment instanceof ArrayElemNode) {
      arrayElem = (ArrayElemNode) assignment;
      isUnsetArrayParam = arrayElem.isUnsetParameterIdArrayElem(symbolTable);
      arrayParam = arrayElem.getUnsetParameterIdArrayElem(symbolTable);
    }


    if (isUnsetParam || isUnsetArrayParam) {

      if (firstCheck) {
        List<DataTypeId> expecteds = new ArrayList<>();
        expecteds.add(new BaseType(BaseType.Type.INT));
        expecteds.add(new BaseType(BaseType.Type.CHAR));

        if (isUnsetParam) {
          param.addToExpectedTypes(expecteds);
        } else if (isUnsetArrayParam) {
          arrayParam.addToExpectedTypes(expecteds);
        }

        boolean paramTypeIsStillNull = (isUnsetParam) ? param.getType() == null
                                           : arrayElem.getType(symbolTable) == null;
        if (paramTypeIsStillNull) {
          uncheckedNodes.add(this);
          return;
        }

      } else {
        if (isUnsetParam) {
          param.setType(DEFAULT_TYPE);
        } else if (isUnsetArrayParam) {
          arrayParam.setBaseElemType(DEFAULT_TYPE);
        }

        firstCheck = true;
      }
    }

    /* Recursively call semanticAnalysis on assignment node */
    assignment.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that the type of assignment is an INT or a CHAR */
     assignmentType = assignment.getType(symbolTable);

    if (assignmentType == null) {
      errorMessages.add(new SemanticError(assignment.getLine(), assignment.getCharPositionInLine(),
          "Could not resolve type for '" + assignment + "."
              + " Expected: INT, CHAR"));

    } else if (!assignmentType.equals(new BaseType(BaseType.Type.INT)) &&
                   !assignmentType.equals(new BaseType(BaseType.Type.CHAR))) {
      errorMessages.add(new SemanticError(assignment.getLine(), assignment.getCharPositionInLine(),
          "Incompatible type for 'read' statement."
              + " Expected: INT, CHAR Actual: " + assignmentType));
    }
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().
                                         visitReadStatementNode(internalState, assignment, getCurrSymTable());
  }

}