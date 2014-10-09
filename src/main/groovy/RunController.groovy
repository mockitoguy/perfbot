import com.yourkit.api.Controller
import com.yourkit.api.MemorySnapshot

class RunController {

  private final PerformanceRun run

  RunController(PerformanceRun run) {
    this.run = run
  }

  void buildAndSnapshot() {
    runBuild()
    snapshot()
  }

  void runBuild() {
    println "Running build..."
    def totalTime = new ProcessRunner(run.dir, run.cmd, run.verbose).run()
    run.buildFinished(totalTime.totalTime)
  }

  void snapshot() {
    Controller controller = new Controller("localhost", 10001);
    controller.forceGC()
    def snapshotFile = new File(controller.captureMemorySnapshot());
    println "Captured snapshot: $snapshotFile"
    def snapshot = new MemorySnapshot(snapshotFile)

    def size = snapshot.getShallowSize("<retained-objects><objects class='*'/></retained-objects>")
    run.newSnapshot(size)
  }
}
