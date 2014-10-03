import com.yourkit.api.Controller
import com.yourkit.api.MemorySnapshot

class RunController {

  private final PerformanceRun run
  Controller controller

  RunController(PerformanceRun run) {
    this.run = run
  }

  void buildAndSnapshot() {
    runBuild()
    snapshot()
  }

  void runBuild() {
    println "Running build..."
    def buildLog = new File(run.dir, "build.log")
    buildLog.createNewFile()
    def proc = new ProcessBuilder(run.cmd)
        .directory(run.dir)
        .redirectErrorStream(true)

    def result = proc.start().waitFor()
    if (result != 0) {
      throw new RuntimeException("Build failure. Build log ($buildLog.absolutePath):\n" + buildLog.text)
    }
    run.builds++

    if (controller == null) {
      controller = new Controller("localhost", 10001);
    }
  }

  void snapshot() {
    assert controller != null : "first run at least one build!"
    controller.forceGC()
    def snapshotFile = new File(controller.captureMemorySnapshot());
    println "Captured snapshot: $snapshotFile"
    def snapshot = new MemorySnapshot(snapshotFile)

    def size = snapshot.getShallowSize("<retained-objects><objects class='*'/></retained-objects>")
    run.newSnapshot(size)
  }
}
