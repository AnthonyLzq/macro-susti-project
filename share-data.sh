rm -rf cluster/shared-data/java-spark-server
rm -rf cluster/shared-data/java-client
rm -rf cluster/shared-data/db

cp -r java-spark-server cluster/shared-data/
cp -r java-client cluster/shared-data/
cp -r db cluster/shared-data/
