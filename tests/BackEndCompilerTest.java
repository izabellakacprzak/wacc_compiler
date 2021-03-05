import java.io.IOException;
import org.junit.Test;

public class BackEndCompilerTest {

  @Test
  public void runBackendTests() {
    try {
      String[] cmds = {"./compile_emulate_tests.sh", "src/test/backend_tests.txt"};
      Process process = new ProcessBuilder(cmds).inheritIO().start();
      process.waitFor();
      int exit = process.exitValue();
      assert (exit == 0);
    } catch (IOException | InterruptedException e) {
      System.out.println("Exception");
    }
  }
}
