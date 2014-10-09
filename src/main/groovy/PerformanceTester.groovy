class PerformanceTester {

  public static void main(String[] args) {
    def run = new PerformanceRun(args)
    def c = run.controller()

    run.warmUpRuns.times {
      c.runBuild()
    }

    run.runs.times {
      c.buildAndSnapshot()
    }

    run.storeResult()
  }
}
