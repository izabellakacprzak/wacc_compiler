import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;


public class SyntaxErrorListener extends BaseErrorListener {

  private final List<String> syntaxErrors;

  public SyntaxErrorListener() {
    syntaxErrors = new ArrayList<>();
  }


  /* Accumulates all the syntax errors that the listener is notified of during parsing and AST visiting
   * and stores the line, position in line and error message of each one in the syntaxErrors list. */
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
      int line, int charPositionInLine, String msg, RecognitionException e) {

    syntaxErrors.add("Syntax error at line " + line + ":" + charPositionInLine + " " + msg);
  }

  /* Prints all the accumulated syntax errors. */
  public void printAllErrors() {
    for (String error : syntaxErrors) {
      System.out.println(error);
    }
  }

  public boolean hasSyntaxErrors() {
    return !syntaxErrors.isEmpty();
  }

  public int syntaxErrorsCount() {
    return syntaxErrors.size();
  }
}
