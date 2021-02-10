package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;
import java.util.List;

public class PairElemNode implements AssignRHSNode {

  private final int line;
  private final int charPositionInLine;

  private final int position; // 0 if FST otherwise SND
  private final ExpressionNode expr;
  private static final int FST = 0;


  public PairElemNode(int line, int charPositionInLine, int position, ExpressionNode expr) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.position = position;
    this.expr = expr;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    expr.semanticAnalysis(symbolTable, errorMessages);

    if (!(expr instanceof IdentifierNode)) {
      errorMessages.add(expr.getLine() + ":" + expr.getCharPositionInLine()
          + " " + expr.toString() + "is not an instance of a Pair");
    } else {
      IdentifierNode pairId = (IdentifierNode) expr;
      DataTypeId expectedType = pairId.getType(symbolTable);
      if (expectedType == null) {
        errorMessages.add(line + ":" + charPositionInLine
                + "Could not resolve expected pair elem type. " );
      } else if(!(expectedType instanceof PairType)) {
        errorMessages.add(expr.getLine() + ":" + expr.getCharPositionInLine()
                + " " + expr.toString() + "is not an instance of a Pair");
      }
    }
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    if (!(expr instanceof IdentifierNode)) {
      return null;
    }

    IdentifierNode pairId = (IdentifierNode) expr;
    DataTypeId pairType = pairId.getType(symbolTable);
    if(!(pairType instanceof PairType)) {
      return null;
    }

    return position == FST ? ((PairType) pairType).getFstType() : ((PairType) pairType).getSndType();
  }
}

