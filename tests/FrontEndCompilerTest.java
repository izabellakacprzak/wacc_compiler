import java.io.IOException;

import org.junit.Test;

public class FrontEndCompilerTest {
  @Test
  public void runTests() {
    try {
      String[] cmds = {"./run_tests.sh"};
      Process process = new ProcessBuilder(cmds).inheritIO().start();
      process.waitFor();
      int exit = process.exitValue();
      assert (exit == 0);
    } catch (IOException | InterruptedException e) {
      System.out.println("Exception");
    }
  }

}
