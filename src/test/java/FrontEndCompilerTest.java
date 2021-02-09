package java;

import java.io.IOException;
import org.junit.Test;

public class FrontEndCompilerTest {
  @Test
  public void runTests() {
    try{
      Process process = new ProcessBuilder("./run_tests.sh").start();
      process.wait();
      int exit = process.exitValue();
      assert(exit == 0);
    } catch (IOException | InterruptedException e){
      System.out.println("Exception");
    }
  }

}
