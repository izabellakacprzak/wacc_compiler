import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;


public class SyntaxErrorListener extends BaseErrorListener {

  private final List<String> syntaxErrors;

  public SyntaxErrorListener() {
    syntaxErrors = new ArrayList<>();
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
                          Object offendingSymbol,
                          int line, int charPositionInLine,
                          String msg,
                          RecognitionException e) {

    syntaxErrors.add("Syntax error at line " + line + ":" + charPositionInLine + " " + msg);
  }

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
