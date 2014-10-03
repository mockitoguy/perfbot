import spock.lang.Specification

class PerformanceRunTest extends Specification {

  def "prints nicely"() {
    expect:
    new PerformanceRun(cmd: ["foo", "bar"], snapshots: [3: 5000000L, 4: 6000000L, 5: 7000000L]).toString() ==
         "'foo bar' -> x3: 5M, x4: 6M, x5: 7M, avg. leak ~1M"
  }

  def "parses arguments"() {
    def r = new PerformanceRun("/Users/sfaber/tmp/foo.txt", "/Users/sfaber/linkedin/network", "ligradle", ":clean", ":history:history-war:build")
    expect:
    r.cmd == ["ligradle", ":clean", ":history:history-war:build"]
    r.resultsFile.absolutePath == "/Users/sfaber/tmp/foo.txt"
    r.dir.absolutePath == "/Users/sfaber/linkedin/network"
  }
}
