package AbstractSyntaxTree.expression;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;

import java.util.List;

import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

public class ArrayElemNode extends ExpressionNode {

  /* identifier:  IdentifierNode representing the identifier of this node
   * expressions: List of ExpressionNodes corresponding to the INT references to
   *                an array element */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> expressions;

  public ArrayElemNode(int line, int charPositionInLine, IdentifierNode identifier,
                       List<ExpressionNode> expressions) {
    super(line, charPositionInLine);
    this.identifier = identifier;
    this.expressions = expressions;
  }

  public IdentifierNode getIdentifier() {
    return identifier;
  }

  public List<ExpressionNode> getExpressions() {
    return expressions;
  }

  public void setArrayElemBaseType(SymbolTable symbolTable, DataTypeId type) {
    if (identifier.isUnsetParamId(symbolTable) || identifier.isUnsetParamArray(symbolTable)) {
      ParameterId paramId = identifier.getParamId(symbolTable);
      int size = expressions.size();

      paramId.setNestedType(type, size);
    }
  }

  public void addToExpectedTypesArrayElem(SymbolTable symbolTable, List<DataTypeId> type) {
    if (identifier.isUnsetParamId(symbolTable) || identifier.isUnsetParamArray(symbolTable)) {
      ParameterId paramId = identifier.getParamId(symbolTable);
      paramId.addToExpectedTypes(type);
    }
  }

  public void addToMatchingParamsArrayElem(SymbolTable symbolTable, ParameterId param) {
    if (identifier.isUnsetParamId(symbolTable) || identifier.isUnsetParamArray(symbolTable)) {
      ParameterId paramId = identifier.getParamId(symbolTable);
      paramId.addToMatchingParams(param);
    }
  }

  public ParameterId getUnsetParameterIdArrayElem(SymbolTable symbolTable) {
    if (identifier.isUnsetParamId(symbolTable) || identifier.isUnsetParamArray(symbolTable)) {
      return identifier.getParamId(symbolTable);
    }
    return null;
  }

  public boolean isUnsetParameterIdArrayElem(SymbolTable symbolTable) {
    return (identifier.isUnsetParamId(symbolTable) || identifier.isUnsetParamArray(symbolTable));
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
                               List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Set the symbol table for this node's scope */
    setCurrSymTable(symbolTable);

    /* If any expression is an unset parameter or an array element of a parameter without a
     *   base type, the type can be inferred to be INT*/
    boolean isUnsetParam;
    boolean isUnsetArrayParam = false;
    for (ExpressionNode expression : expressions) {
      isUnsetParam = expression.isUnsetParamId(symbolTable);
      if (expression instanceof ArrayElemNode) {
        isUnsetArrayParam = expression.isUnsetParamArray(symbolTable);
      }

      if (isUnsetParam) {
        expression.getParamId(symbolTable).setType(new BaseType(INT));
      } else if (isUnsetArrayParam && expression instanceof ArrayElemNode) {
        ((ArrayElemNode) expression).setArrayElemBaseType(symbolTable, new BaseType(INT));
      }
      expression.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
    }

    /* Check that each expression is of type INT */
    DataTypeId thisType;
    for (ExpressionNode expression : expressions) {
      thisType = expression.getType(symbolTable);

      if (thisType == null) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Could not resolve type of '" + expression + "' in ARRAY ELEM."
                + " Expected: INT"));
        break;
      }

      if (!thisType.equals(new BaseType(BaseType.Type.INT))) {
        errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
            "Incompatible type of '" + expression + "' in ARRAY ELEM."
                + " Expected: INT Actual: " + thisType));
      }
    }


//TODO: ask Una how to use firstCheck in here
    if (identifier.isUnsetParamId(symbolTable)) {
      if (identifier.getType(symbolTable) == null) {
        ParameterId param = identifier.getParamId(symbolTable);
        param.setType(new ArrayType());
        uncheckedNodes.add(identifier);
        return;
      }
    }


    /* Recursively call semanticAnalysis on stored nodes */
    identifier.semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);


    /* Check identifier has been declared and is of an ARRAY type */
    Identifier idType = symbolTable.lookupAll(identifier.getIdentifier());

    if (idType == null) {
      idType = symbolTable.lookupAll("attr*" + identifier.getIdentifier());
      if (idType == null) {
        errorMessages.add(
            new SemanticError(
                super.getLine(),
                super.getCharPositionInLine(),
                "No declaration of '"
                    + identifier.getIdentifier()
                    + "' identifier."
                    + " Expected: ARRAY IDENTIFIER."));
        return;
      }
    }

    if (!(identifier.getType(symbolTable) instanceof ArrayType)) {
      System.out.println(identifier);
      errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
          "Incompatible type of '" + identifier.getIdentifier() + "' identifier."
              + " Expected: ARRAY IDENTIFIER Actual: " + idType));
      return;
    }


  }

  @Override
  public void generateAssembly(InternalState internalState) {
    internalState.getCodeGenVisitor().visitArrayElemNode(internalState, this);
  }

  /* Return the type of the elements stored in identifier array */
  /*If when this function is called, the type of the identifier is null, set its type to ArrayType
  as it reached this node. */
  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    DataTypeId idType = identifier.getType(symbolTable);
    if (idType == null && identifier.isUnsetParamId(symbolTable)) {
      ParameterId param = identifier.getParamId(symbolTable);
      param.setNestedType(null, expressions.size());
      return identifier.getType(symbolTable);
    }
    if (!(idType instanceof ArrayType)) {
      return null;
    }

    DataTypeId elemType = idType;
    int size = expressions.size();
    while (size > 0) {
      if (!(elemType instanceof ArrayType)) {
        elemType = null;
        break;
      }

      elemType = ((ArrayType) elemType).getElemType();

      size--;
    }

    return elemType;
  }

  /* Returns a ArrayElem in the form: array_id[expr1][expr2]...[exprN] */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();

    str.append(identifier.getIdentifier()).append("[");

    for (ExpressionNode expression : expressions) {
      str.append(expression).append("][");
    }

    str.deleteCharAt(str.length() - 1);

    return str.toString();
  }
}
