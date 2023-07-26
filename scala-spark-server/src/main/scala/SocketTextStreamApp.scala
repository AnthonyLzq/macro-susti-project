import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel
import org.apache.spark.ml.feature.{StringIndexerModel, VectorAssembler}

object SocketTextStreamApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("SocketTextStreamApp").config("spark.sql.streaming.console.row.max", 10000).getOrCreate()

    val lines = spark.readStream.format("socket").option("host", "localhost").option("port", 9090).load()

    import spark.implicits._

    val schema = StructType(Array(
      StructField("work_year", IntegerType),
      StructField("experience_level", StringType),
      StructField("employment_type", StringType),
      StructField("job_title", StringType),
      StructField("salary", IntegerType),
      StructField("salary_currency", StringType),
      StructField("salary_in_usd", IntegerType),
      StructField("employee_residence", StringType),
      StructField("remote_ratio", IntegerType),
      StructField("company_location", StringType),
      StructField("company_size", StringType)
    ))

    val data = lines.as[String].map(_.split(","))
      .map(array => (array(0).toInt, array(1), array(2), array(3), array(4).toInt, array(5), array(6).toInt, array(7), array(8).toInt, array(9), array(10)))
      .toDF(schema.fieldNames: _*)

    val model = MultilayerPerceptronClassificationModel.load("/opt/spark/work-dir/models/perceptron")

    val jobTitleIndexerModel = StringIndexerModel.load("/opt/spark/work-dir/models/jobTitleIndexerModel")
    val employmentTypeIndexerModel = StringIndexerModel.load("/opt/spark/work-dir/models/employmentTypeIndexerModel")
    val employmentResidenceIndexerModel = StringIndexerModel.load("/opt/spark/work-dir/models/employmentResidenceIndexerModel")
    val companyLocationIndexerModel = StringIndexerModel.load("/opt/spark/work-dir/models/companyLocationIndexerModel")
    val companySizeIndexerModel = StringIndexerModel.load("/opt/spark/work-dir/models/companySizeIndexerModel")

    val indexed = jobTitleIndexerModel.transform(data)
    val indexed2 = employmentTypeIndexerModel.transform(indexed)
    val indexed3 = employmentResidenceIndexerModel.transform(indexed2)
    val indexed4 = companyLocationIndexerModel.transform(indexed3)
    val finalData = companySizeIndexerModel.transform(indexed4)

    val assembler = new VectorAssembler()
      .setInputCols(
        Array(
          "work_year",
          "employment_type_indexed",
          "job_title_indexed",
          "employee_residence_indexed",
          "remote_ratio",
          "company_location_indexed",
          "company_size_indexed",
          "salary_in_usd"
        )
      )
      .setOutputCol("features")

    val dataWithFeatures = assembler.transform(finalData)

    val predictions = model.transform(dataWithFeatures)

    predictions
      .select(
        "work_year",
        "employment_type",
        "job_title",
        "salary_in_usd",
        "employee_residence",
        "remote_ratio",
        "company_location",
        "company_size",
        "prediction"
      )
      .writeStream
      .format("console")
      .start()

    // Select only the "prediction" column to display
    val predictionColumn = predictions.select("prediction")

    val query = predictionColumn
      .writeStream
      .outputMode("append")
      .format("csv")
      .option("path", "/opt/spark/work-dir/predictions/perceptron")
      .option("checkpointLocation", "/opt/spark/work-dir/predictions")
      .start()

    query.awaitTermination()
  }
}