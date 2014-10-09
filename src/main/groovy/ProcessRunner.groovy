import java.io.File
import java.io.IOException
import java.util.List

class ProcessRunner {

  private final File workDir
  private final List<String> commandLine
  private final boolean verbose

  ProcessRunner(File workDir, List<String> commandLine, boolean verbose) {
    this.workDir = workDir
    this.commandLine = commandLine
    this.verbose = verbose
  }

  void run() throws IOException {
    def buildLog = new File(workDir, "build.log")
    buildLog.createNewFile()
    def out = verbose? System.out : new PrintStream(new ByteArrayOutputStream())
    def p = new ProcessBuilder(commandLine).directory(workDir).redirectErrorStream(true).start()
    def t = Thread.start{
      p.getInputStream().withReader { Reader r ->
        String line = r.readLine()
        while(line != null) {
          out.println(line)
          line = r.readLine()
        }
      }
    }
    def result = p.waitFor()
    t.join()
    if (result != 0) {
      String message = "*** PERFBOT: Build failure."
      if (!verbose) {
        message += "\n----------------------\n" + out.toString()
      }
      throw new RuntimeException(message)
    }
  }
}
