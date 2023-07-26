import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.classification.LinearSVC
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorAssembler}

object ScalaModel {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("LinearSVCExample").getOrCreate()

    // Cargamos los datos
    val data = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("/opt/spark/work-dir/db/salaries_to_train_2.csv")

    // Renombramos la columna "experience_level" a "label"
    val renamedData = data.withColumnRenamed("experience_level", "label")

    // Convertimos la variable "label" a numérica
    val labelIndexer = new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(renamedData)
    var finalData = labelIndexer.transform(renamedData)

    // Convertimos las variables categóricas a numéricas
    val jobTitleIndexer = new StringIndexer().setInputCol("job_title").setOutputCol("job_title_indexed").setHandleInvalid("keep")
    val employmentTypeIndexer = new StringIndexer().setInputCol("employment_type").setOutputCol("employment_type_indexed").setHandleInvalid("keep")
    val employmentResidenceIndexer = new StringIndexer().setInputCol("employee_residence").setOutputCol("employee_residence_indexed").setHandleInvalid("keep")
    val companyLocationIndexer = new StringIndexer().setInputCol("company_location").setOutputCol("company_location_indexed").setHandleInvalid("keep")
    val companySizeIndexer = new StringIndexer().setInputCol("company_size").setOutputCol("company_size_indexed").setHandleInvalid("keep")

    val jobTitleIndexerModel = jobTitleIndexer.fit(finalData)
    val employmentTypeIndexerModel = employmentTypeIndexer.fit(finalData)
    val employmentResidenceIndexerModel = employmentResidenceIndexer.fit(finalData)
    val companyLocationIndexerModel = companyLocationIndexer.fit(finalData)
    val companySizeIndexerModel = companySizeIndexer.fit(finalData)

    jobTitleIndexerModel.save("/opt/spark/work-dir/models/jobTitleIndexerModel2")
    employmentTypeIndexerModel.save("/opt/spark/work-dir/models/employmentTypeIndexerModel2")
    employmentResidenceIndexerModel.save("/opt/spark/work-dir/models/employmentResidenceIndexerModel2")
    companyLocationIndexerModel.save("/opt/spark/work-dir/models/companyLocationIndexerModel2")
    companySizeIndexerModel.save("/opt/spark/work-dir/models/companySizeIndexerModel2")

    val indexed = jobTitleIndexerModel.transform(finalData)
    val indexed2 = employmentTypeIndexerModel.transform(indexed)
    val indexed3 = employmentResidenceIndexerModel.transform(indexed2)
    val indexed4 = companyLocationIndexerModel.transform(indexed3)
    finalData = companySizeIndexerModel.transform(indexed4)

    // Vectorizamos las características
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

    val output = assembler.transform(finalData)

    // Dividimos los datos en conjuntos de entrenamiento y prueba
    val Array(trainingData,
    testData) = output.randomSplit(Array(0.7, 0.3))

    // Creamos el entrenador y configuramos sus parámetros
    val trainer = new LinearSVC()
      .setMaxIter(100)
      .setRegParam(0.1)
      .setLabelCol("indexedLabel") // utiliza la columna "indexedLabel"

    val model = trainer.fit(trainingData)

    model.write.overwrite().save("/opt/spark/work-dir/models/linearSVC")

    val result = model.transform(testData)
    val predictionAndLabels = result.select("prediction", "indexedLabel") // utiliza la columna "indexedLabel"

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel") // utiliza la columna "indexedLabel"
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    println(s"Test set accuracy = ${evaluator.evaluate(predictionAndLabels)}")
  }
}