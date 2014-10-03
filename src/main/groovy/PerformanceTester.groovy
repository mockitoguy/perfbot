class PerformanceTester {

  public static void main(String[] args) {
    def run = new PerformanceRun(args)
    def c = run.controller()

    c.runBuild()
    c.runBuild()

    c.buildAndSnapshot()
    c.buildAndSnapshot()
    c.buildAndSnapshot()

    run.storeResult()
  }
}
