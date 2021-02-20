import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import org.hamcrest.*;

public class BackEndCompilerTest {
  private String[] compileCmds = {"./compile"};
  private String[] assembleCmds = {"arm-linux-gnueabi-gcc", "-o", "FILENAME1", "-mcpu=arm1176jzf-s",
      "-mtune=arm1176jzf-s"};
  private String[] emulateCmds = {"qemu-arm -L", "/usr/arm-linux-gnueabi/"};

  @Test
  public void runBackendTests() {
    File waccFiles = new File("src/test/valid");
    listOfFiles(waccFiles);
  }

  public void listOfFiles(File dirPath) {
    File filesList[] = dirPath.listFiles();
    for (File file : filesList) {
      if (file.isFile()) {
        try {
          Process compileProcess = new ProcessBuilder(Arrays.toString(compileCmds) + file.getAbsolutePath()).start();
          compileProcess.wait();
          String assemblyFile = file.getName().replace(".wacc", ".s");
          Process assebleProcess = new ProcessBuilder(Arrays.toString(assembleCmds) + assemblyFile).start();
          assebleProcess.wait();
          String emulateFile = file.getName().replace(".wacc", "");
          Process emulateProcess = new ProcessBuilder(Arrays.toString(emulateCmds) + emulateFile).start();
          emulateProcess.wait();
          // check for output and exit code
        } catch (IOException | InterruptedException e) {
          System.out.println("Cannot compile a .wacc file");
        }
      } else {
        listOfFiles(file);
      }
    }
  }
}
