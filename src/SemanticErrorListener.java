import java.util.ArrayList;
import java.util.List;

public class SemanticErrorListener {

  private final List<String> semanticErrors;

  public SemanticErrorListener() {
    this.semanticErrors = new ArrayList<>();
  }

  public List<String> getList() {
    return semanticErrors;
  }

  public void printAllErrors() {
    for (String error : semanticErrors) {
      System.out.println("Semantic error at line " + error);
    }
  }

  public boolean hasSemanticErrors() {
    return !semanticErrors.isEmpty();
  }


  public int semanticErrorsCount() {
    return semanticErrors.size();
  }
}
