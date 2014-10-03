class PerformanceRun {
  int builds = 0
  Map<Integer, Long> snapshots = new LinkedHashMap()
  List<String> cmd = ["ligradle", ":clean", ":history:history-war:build"]
  File resultsFile
  File dir

  PerformanceRun() {

  }

  PerformanceRun(String[] args) {
    String filePath = args[0]
    this.resultsFile = new File(filePath).absoluteFile
    this.resultsFile.parentFile.mkdirs()
    assert this.resultsFile.isFile() || this.resultsFile.createNewFile()
    println "Results will be stored in " + filePath

    String dirPath = args[1]
    this.dir = new File(dirPath).absoluteFile
    assert this.dir.isDirectory() : "Incorrect working directory: $dirPath"

    cmd = args[2..-1]
    assert cmd.size() > 0 : "Command not supplied"
    println "Command: '" + cmd.join(" ") + "'"
  }

  RunController controller() {
    new RunController(this)
  }

  void newSnapshot(long size) {
    snapshots[builds] = size
  }

  static int mega(long bytes) {
    def mega = bytes / (1024 * 1024)
    String.format("%.0fM", (float) mega)
  }

  String toString() {
    def history = snapshots.collect { k,v -> "x" + k + ": " + mega(v) }.join(", ")
    def previous = 0
    def total = 0
    snapshots.each { k,v ->
      if (previous == 0) {
        previous = v
      } else {
        total += v - previous
        previous = v
      }
    }
    "'" + cmd.join(" ") + "' -> " + history + ", avg. leak ~" + mega((long) total/(snapshots.size() - 1))
  }

  void storeResult() {
    def result = toString()
    println result
    resultsFile.parentFile.mkdirs()
    resultsFile.createNewFile()
    resultsFile.text = resultsFile.text + "\n" + result
  }
}
