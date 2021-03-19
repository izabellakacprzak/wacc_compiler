package AbstractSyntaxTree.statement;

import static SemanticAnalysis.DataTypes.BaseType.Type.BOOL;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ArrayElemNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ForStatementNode extends StatementNode {

  /* declaration:   DeclarationStatementNode of the loop variant
   * condition:     ExpressionNode representing the condition of the for loop
   * condStatement: StatementNode representing the statement which modifies the loop variant
   * bodyStatement: StatementNode representing the body of the for loop */
  private final DeclarationStatementNode declaration;
  private final ExpressionNode condition;
  private final StatementNode condStatement;
  private final StatementNode bodyStatement;

  public ForStatementNode(DeclarationStatementNode declaration, ExpressionNode condition,
      StatementNode condStatement, StatementNode bodyStatement) {
    this.declaration = declaration;
    this.condition = condition;
    this.condStatement = condStatement;
    this.bodyStatement = bodyStatement;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);
    DataTypeId conditionType = condition.getType(symbolTable);


    /* Call semanticAnalysis on the declaration statement */
    declaration.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    boolean isUnsetParam = condition.isUnsetParamId(symbolTable);
    ParameterId param = condition.getParamId(symbolTable);

    boolean isUnsetArrayParam = false;
    ParameterId arrayParam = null;
    ArrayElemNode arrayElem = null;


    if (condition instanceof ArrayElemNode) {
      arrayElem = (ArrayElemNode) condition;
      isUnsetArrayParam = arrayElem.isUnsetParameterIdArrayElem(symbolTable);
      arrayParam = arrayElem.getUnsetParameterIdArrayElem(symbolTable);
    }

    if (isUnsetParam) {
      param.setType(new BaseType(BOOL));
    } else if (isUnsetArrayParam) {
      arrayParam.setBaseElemType(new BaseType(BOOL));
    }

    /* Recursively call semanticAnalysis on condition node */
    condition.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);

    /* Check that the type of the condition expression is BOOL */
    conditionType = condition.getType(symbolTable);

    if (conditionType == null) {
      errorMessages.add(new SemanticError(condition.getLine(), condition.getCharPositionInLine(),
          "Could not resolve type of '" + condition + "'."
              + " Expected: BOOL"));

    } else if (!conditionType.equals(new BaseType(BaseType.Type.BOOL))) {
      errorMessages.add(new SemanticError(condition.getLine(), condition.getCharPositionInLine(),
          "Incompatible type for 'For' condition."
              + " Expected: BOOL Actual: " + conditionType));
    }

    SymbolTable innerScope = new SymbolTable(symbolTable);
    innerScope.add(declaration.getIdentifierVar().getNode().getIdentifier(), declaration.getIdentifierVar());
    /* Call semanticAnalysis on condition statement node */
    condStatement
        .semanticAnalysis(innerScope, errorMessages, uncheckedNodes, firstCheck);

    /* Recursively call semanticAnalysis on statement node */
    bodyStatement
        .semanticAnalysis(innerScope, errorMessages, uncheckedNodes, firstCheck);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitForStatementNode(internalState, declaration, condition,
        bodyStatement, condStatement);
  }
}
