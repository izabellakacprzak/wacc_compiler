package AbstractSyntaxTree.expression;

import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class ArrayElemNode extends ExpressionNode {

  /* identifier:  IdentifierNode representing the identifier of this node
   * expressions: List of ExpressionNodes corresponding to the INT references to an array element */
  private final IdentifierNode identifier;
  private final List<ExpressionNode> expressions;
  private SymbolTable currSymTable = null;

  public ArrayElemNode(int line, int charPositionInLine, IdentifierNode identifier,
      List<ExpressionNode> expressions) {
    super(line, charPositionInLine);
    this.identifier = identifier;
    this.expressions = expressions;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    /* Recursively call semanticAnalysis on stored nodes */
    identifier.semanticAnalysis(symbolTable, errorMessages);

    for (ExpressionNode expression : expressions) {
      expression.semanticAnalysis(symbolTable, errorMessages);
    }

    /* Check identifier has been declared and is of an ARRAY type */
    Identifier idType = symbolTable.lookupAll(identifier.getIdentifier());

    if (idType == null) {
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " No declaration of '" + identifier.getIdentifier() + "' identifier."
          + " Expected: ARRAY IDENTIFIER.");
      return;
    }

    if (!(identifier.getType(symbolTable) instanceof ArrayType)) {
      System.out.println(identifier);
      errorMessages.add(super.getLine() + ":" + super.getCharPositionInLine()
          + " Incompatible type of '" + identifier.getIdentifier() + "' identifier."
          + " Expected: ARRAY IDENTIFIER Actual: " + idType);
      return;
    }

    /* Check that each expression is of type INT */
    DataTypeId thisType;
    for (ExpressionNode expression : expressions) {
      thisType = expression.getType(symbolTable);

      if (thisType == null) {
        errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
            + " Could not resolve type of '" + expression + "' in ARRAY ELEM."
            + " Expected: INT");
        break;
      }

      if (!thisType.equals(new BaseType(BaseType.Type.INT))) {
        errorMessages.add(expression.getLine() + ":" + expression.getCharPositionInLine()
            + " Incompatible type of '" + expression + "' in ARRAY ELEM."
            + " Expected: INT Actual: " + thisType);
      }
    }
    currSymTable = symbolTable;
  }

  @Override
  public void generateAssembly(InternalState internalState) {

  }

  /* Return the type of the elements stored in identifier array */
  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    DataTypeId idType = identifier.getType(symbolTable);
    if (!(idType instanceof ArrayType)) {
      return null;
    }

    return ((ArrayType) idType).getElemType();
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
