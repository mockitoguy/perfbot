import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PerformanceRunTest extends Specification {

  @Rule TemporaryFolder temp = new TemporaryFolder()

  def "prints nicely"() {
    expect:
    new PerformanceRun(cmd: ["foo", "bar"], snapshots: [3: 5000000L, 4: 6000000L, 5: 7000000L]).toString() ==
         "'foo bar' -> x3: 5M, x4: 6M, x5: 7M, avg. leak ~1M, times: []"
  }

  def "parses arguments"() {
    def results = temp.newFile("results.txt")
    def projectDir = temp.newFolder("project")

    def r = new PerformanceRun(results.toString(), projectDir.toString(), "gradle", "clean", ":someProject:build")
    expect:
    r.cmd == ["gradle", "clean", ":someProject:build"]
    r.resultsFile.absolutePath == results.absolutePath
    r.dir.absolutePath == projectDir.absolutePath
    !r.verbose
  }

  def "knows verbose mode"() {
    def results = temp.newFile("results.txt")
    def projectDir = temp.newFolder("project")

    def r = new PerformanceRun(results.toString(), projectDir.toString(), "-v", "gradle", "clean", ":someProject:build")
    expect:
    r.cmd == ["gradle", "clean", ":someProject:build"]
    r.resultsFile.absolutePath == results.absolutePath
    r.dir.absolutePath == projectDir.absolutePath
    r.verbose
    r.warmUpRuns == 2
    r.runs == 3
  }

  def "parses no of runs"() {
    def results = temp.newFile("results.txt")
    def projectDir = temp.newFolder("project")

    def r = new PerformanceRun(results.toString(), projectDir.toString(), "--warmUpRuns=1", "--runs=5", "gradle", "clean", ":someProject:build")
    expect:
    r.cmd == ["gradle", "clean", ":someProject:build"]
    r.warmUpRuns == 1
    r.runs == 5
  }
}
