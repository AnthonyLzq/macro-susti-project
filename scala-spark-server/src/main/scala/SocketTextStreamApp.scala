import org.apache.spark.sql.SparkSession

object SocketTextStreamApp {
  def main(args: Array[String]): Unit = {
    // Update the path to the build.sbt file in the host machine's shared volume
    //    val logFile = "/opt/bitnami/spark/data/build.sbt"
    //    val spark = SparkSession.builder
    //      .appName("Simple Application")
    //      .getOrCreate()
    //
    //    val logData = spark.read.textFile(logFile).cache()
    //    val numAs = logData.filter(line => line.contains("a")).count()
    //    val numBs = logData.filter(line => line.contains("b")).count()
    //    println(s"Lines with a: $numAs, Lines with b: $numBs")
    //    spark.stop()

    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val lines = spark
      .readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9090)
      .load()

    import spark.implicits._

    val words = lines.as[String].flatMap(_.split(" "))

    val wordCounts = words.groupBy("value").count()

    val query = wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query.awaitTermination()
  }
}
