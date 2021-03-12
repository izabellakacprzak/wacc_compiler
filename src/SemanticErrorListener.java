import SemanticAnalysis.SemanticError;
import java.util.ArrayList;
import java.util.List;

public class SemanticErrorListener {

  private final List<SemanticError> semanticErrors;

  public SemanticErrorListener() {
    this.semanticErrors = new ArrayList<>();
  }

  public List<SemanticError> getList() {
    return semanticErrors;
  }

  public void printAllErrors() {
    semanticErrors.sort(SemanticError::compareTo);

    for (SemanticError error : semanticErrors) {
      System.out.println(error);
    }
  }

  public boolean hasSemanticErrors() {
    return !semanticErrors.isEmpty();
  }


  public int semanticErrorsCount() {
    return semanticErrors.size();
  }
}
