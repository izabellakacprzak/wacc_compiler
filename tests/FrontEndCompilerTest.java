import java.io.IOException;

import org.junit.Test;

public class FrontEndCompilerTest {
  @Test
  public void runValidTests() {
    try {
      String[] cmds = {"./run_tests.sh", "valid", "src/test/valid_tests.txt", "0"};
      Process process = new ProcessBuilder(cmds).inheritIO().start();
      process.waitFor();
      int exit = process.exitValue();
      assert (exit == 0);
    } catch (IOException | InterruptedException e) {
      System.out.println("Exception");
    }
  }

  @Test
  public void runSyntaxErrorTests() {
    try {
      String[] cmds = {"./run_tests.sh", "syntax error", "src/test/syntax_error_tests.txt", "100"};
      Process process = new ProcessBuilder(cmds).inheritIO().start();
      process.waitFor();
      int exit = process.exitValue();
      assert (exit == 0);
    } catch (IOException | InterruptedException e) {
      System.out.println("Exception");
    }
  }

  @Test
  public void runSemanticErrorTests() {
    try {
      String[] cmds = {"./run_tests.sh", "semantic error", "src/test/semantic_error_tests.txt", "200"};
      Process process = new ProcessBuilder(cmds).inheritIO().start();
      process.waitFor();
      int exit = process.exitValue();
      assert (exit == 0);
    } catch (IOException | InterruptedException e) {
      System.out.println("Exception");
    }
  }
}
