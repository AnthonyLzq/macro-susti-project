cp -r db cluster/shared-data/

rm cluster/shared-data/*.jar
cd scala-spark-server
sbt package
cp target/scala-2.12/scala-spark-server_2.12-1.0.jar ../cluster/shared-data/scala-spark-server.jar

cd ../scala-spark-server-2
sbt package
cp target/scala-2.12/scala-spark-server-2_2.12-1.0.jar ../cluster/shared-data/scala-spark-server-2.jar

cd ../scala-model
sbt package
cp target/scala-2.12/scala-model_2.12-1.0.jar ../cluster/shared-data/scala-model.jar

cd ../scala-model-2
sbt package
cp target/scala-2.12/scala-model-2_2.12-1.0.jar ../cluster/shared-data/scala-model-2.jar
