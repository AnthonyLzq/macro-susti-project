rm cluster/shared-data/*.jar
cd scala-spark-server
sbt package
cp target/scala-2.12/scala-spark-server_2.12-1.0.jar ../cluster/shared-data/scala-spark-server.jar
