import java.io.IOException;
import org.junit.Test;

public class BackEndCompilerTest {
  private String[] compileCmds = {"../compile"};
  private String[] assembleCmds = {"arm-linux-gnueabi-gcc", "-o", "FILENAME1", "-mcpu=arm1176jzf-s",
      "-mtune=arm1176jzf-s"};
  private String[] emulateCmds = {"qemu-arm -L", "/usr/arm-linux-gnueabi/"};

  @Test
  public void runBackendTests() {
    try {
      Process process = new ProcessBuilder("./compile_emulate_tests.sh").inheritIO().start();
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
