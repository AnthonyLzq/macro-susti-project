package server;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

class SparkApp {
  private static final Logger LOGGER = LogManager.getLogger(SparkApp.class);
  public static void main(String[] args) throws Exception {
    try {
      LOGGER.info("Starting Spark Configuration");
      SparkConf sparkConf = new SparkConf()
        .setAppName("SparkApp")
        .setMaster("spark://0.0.0.0:7077");

      LOGGER.info("Starting Spark Streaming Application\n");
      JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));

      JavaReceiverInputDStream<String> lines = ssc.socketTextStream("localhost", 9090);
      // Imprimir cada línea a la consola
      lines.foreachRDD(rdd -> {
        rdd.foreach(line -> {
          LOGGER.info("Received data: " + line);
        });
      });

      ssc.start();
      ssc.awaitTermination();
      LOGGER.info("Spark Streaming Application Terminated");
    } catch (Exception e) {
      LOGGER.error("Exception in SparkApp-main: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
