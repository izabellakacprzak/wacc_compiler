package SemanticAnalysis;

public class SemanticError implements Comparable<SemanticError> {

  private final int line;
  private final int charPositionInLine;
  private final String message;

  public SemanticError(int line, int charPositionInLine, String message) {
    this.line = line;
    this.charPositionInLine = charPositionInLine;
    this.message = message;
  }

  public int getLine() {
    return line;
  }

  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public int compareTo(SemanticError error) {
    if (line != error.getLine()) {
      return line - error.getLine();
    }

    return charPositionInLine - error.getCharPositionInLine();
  }

  @Override
  public String toString() {
    return "Semantic error at line " + line + ":" + charPositionInLine + " " + message;
  }
}
