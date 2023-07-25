# Spark Cluster (Proyecto Sustitutorio)

Proyecto sustitutorio del curso de `Análisis en Macrodatos`.

## Requisitos

Tener [docker](https://docs.docker.com/desktop/install/linux-install/) y [docker-compose](https://docs.docker.com/compose/install/linux/) instalados en su sistema.

## Instalación

```sh
sh run.sh
```

## Cliente Java

La configuración actual del proyecto nos permite correr un solo archivo Java y sus dependencias en lugar de todo el proyecto. Esto es logrado gracias a la siguiente configuración en el [pom.xml](java-client/pom.xml):

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>exec-maven-plugin</artifactId>
      <version>1.6.0</version>
      <configuration>
        <!--Aquí cambiamos la clase que deseamos ejecutar-->
        <mainClass>client.SparkAppSender</mainClass>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Para poder utilizar el cliente Java creado dentro de nuestro cluster de Spark, necesitamos copiar el contenido de la carpeta [java-client](java-client/) y la carpeta [db](db/) a la carpeta [cluster/shared-data](cluster/shared-data/). Esto se hace automáticamente si estamos usando VSCode y tenemos instalada la extensión [Run on save](https://marketplace.visualstudio.com/items?itemName=emeraldwalk.RunOnSave#:~:text=Run%20On%20Save%20for%20Visual,don't%20trigger%20the%20commands.), sino la tenemos instalada (o no estamos usando VSCode), debemos correr los siguientes comandos en nuestra terminal:

```sh
cp -r java-client cluster/shared-data/
cp -r db cluster/shared-data/
```

Una vez copiadas las carpetas, debemos ingresar a nuestro cluster, para eso utilizamos:

```sh
# Ingresar al contenedor que tiene spark
docker exec -it master-node zsh

# Ingresar al cliente de Java
cd java-client

# Instalar dependencias del cliente
mvn clean install

# Ejecutar el cliente
mvn exec:java
```

Una vez hecho esto, tendremos un output similar al siguiente:

```sh
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------------------< client:java-client >-------------------------
[INFO] Building java-client 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- exec-maven-plugin:1.6.0:java (default-cli) @ java-client ---
2023-07-25 07:10:57 INFO Listening on address: 0.0.0.0
2023-07-25 07:10:57 INFO Listening on port: 9090
```

Así, tendremos listo nuestro cliente a la espera de recibir conexiones y enviar la data.

## Servidor Scala

Para utilizar nuestro servidor Scala y consumir la data enviada por nuestro cliente utilizando [Apache Spark Streaming](https://spark.apache.org/docs/latest/streaming-programming-guide.html), debemos copiar el jar generado dentro de la carpeta target, localizada en la carpeta [scala-spark-server](scala-spark-server/). Esto se hace automáticamente si estamos usando VSCode y tenemos instalada la extensión [Run on save](https://marketplace.visualstudio.com/items?itemName=emeraldwalk.RunOnSave#:~:text=Run%20On%20Save%20for%20Visual,don't%20trigger%20the%20commands.), sino la tenemos instalada (o no estamos usando VSCode), debemos correr los siguientes comandos en nuestra terminal:

```sh
# Eliminamos cualquier jar ya existente
rm cluster/shared-data/*.jar

# Entramos a la carpeta del servidor
cd scala-spark-server

# Compilamos el proyecto
sbt package

# Copiamos el jar
cp target/scala-2.12/scala-spark-server_2.12-1.0.jar ../cluster/shared-data/scala-spark-server.jar
```

Una vez copiado el jar, debemos ingresar a nuestro cluster, para eso utilizamos:

```sh
# Ingresar al contenedor que tiene spark
docker exec -it master-node zsh

# Ejecutar el jar
../bin/spark-submit --class SocketTextStreamApp scala-spark-server.jar
```

Una vez hecho esto, tendremos un output similar al siguiente:

```sh
Using Spark's default log4j profile: org/apache/spark/log4j-defaults.properties
23/07/25 07:16:07 INFO SparkContext: Running Spark version 3.2.1
23/07/25 07:16:07 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
23/07/25 07:16:07 INFO ResourceUtils: ==============================================================
23/07/25 07:16:07 INFO ResourceUtils: No custom resources configured for spark.driver.
23/07/25 07:16:07 INFO ResourceUtils: ==============================================================
23/07/25 07:16:07 INFO SparkContext: Submitted application: StructuredNetworkWordCount
23/07/25 07:16:08 INFO ResourceProfile: Default ResourceProfile created, executor resources: Map(cores -> name: cores, amount: 1, script: , vendor: , memory -> name: memory, amount: 1024, script: , vendor: , offHeap -> name: offHeap, amount: 0, script: , vendor: ), task resources: Map(cpus -> name: cpus, amount: 1.0)
23/07/25 07:16:08 INFO ResourceProfile: Limiting resource is cpu
23/07/25 07:16:08 INFO ResourceProfileManager: Added ResourceProfile id: 0
23/07/25 07:16:08 INFO SecurityManager: Changing view acls to: root
23/07/25 07:16:08 INFO SecurityManager: Changing modify acls to: root
23/07/25 07:16:08 INFO SecurityManager: Changing view acls groups to: 
23/07/25 07:16:08 INFO SecurityManager: Changing modify acls groups to: 
23/07/25 07:16:08 INFO SecurityManager: SecurityManager: authentication disabled; ui acls disabled; users  with view permissions: Set(root); groups with view permissions: Set(); users  with modify permissions: Set(root); groups with modify permissions: Set()
23/07/25 07:16:08 INFO Utils: Successfully started service 'sparkDriver' on port 37911.
23/07/25 07:16:08 INFO SparkEnv: Registering MapOutputTracker
23/07/25 07:16:08 INFO SparkEnv: Registering BlockManagerMaster
23/07/25 07:16:08 INFO BlockManagerMasterEndpoint: Using org.apache.spark.storage.DefaultTopologyMapper for getting topology information
23/07/25 07:16:08 INFO BlockManagerMasterEndpoint: BlockManagerMasterEndpoint up
23/07/25 07:16:08 INFO SparkEnv: Registering BlockManagerMasterHeartbeat
23/07/25 07:16:08 INFO DiskBlockManager: Created local directory at /tmp/blockmgr-972c6d31-2a9c-4c5c-a6fa-9a93efce2e10
23/07/25 07:16:08 INFO MemoryStore: MemoryStore started with capacity 366.3 MiB
23/07/25 07:16:08 INFO SparkEnv: Registering OutputCommitCoordinator
23/07/25 07:16:08 INFO Utils: Successfully started service 'SparkUI' on port 4040.
23/07/25 07:16:08 INFO SparkUI: Bound SparkUI to 0.0.0.0, and started at http://cc4392cd314e:4040
23/07/25 07:16:08 INFO SparkContext: Added JAR file:/opt/spark/work-dir/scala-spark-server.jar at spark://cc4392cd314e:37911/jars/scala-spark-server.jar with timestamp 1690269367839
23/07/25 07:16:08 INFO Executor: Starting executor ID driver on host cc4392cd314e
23/07/25 07:16:08 INFO Executor: Fetching spark://cc4392cd314e:37911/jars/scala-spark-server.jar with timestamp 1690269367839
23/07/25 07:16:08 INFO TransportClientFactory: Successfully created connection to cc4392cd314e/172.20.0.2:37911 after 20 ms (0 ms spent in bootstraps)
23/07/25 07:16:08 INFO Utils: Fetching spark://cc4392cd314e:37911/jars/scala-spark-server.jar to /tmp/spark-227b6809-1d7a-4a09-9ff5-f1ca00690453/userFiles-7a639281-48e9-4174-9be3-c15b344c4f6a/fetchFileTemp6190689514620221035.tmp
23/07/25 07:16:08 INFO Executor: Adding file:/tmp/spark-227b6809-1d7a-4a09-9ff5-f1ca00690453/userFiles-7a639281-48e9-4174-9be3-c15b344c4f6a/scala-spark-server.jar to class loader
23/07/25 07:16:08 INFO Utils: Successfully started service 'org.apache.spark.network.netty.NettyBlockTransferService' on port 34917.
23/07/25 07:16:08 INFO NettyBlockTransferService: Server created on cc4392cd314e:34917
23/07/25 07:16:08 INFO BlockManager: Using org.apache.spark.storage.RandomBlockReplicationPolicy for block replication policy
23/07/25 07:16:08 INFO BlockManagerMaster: Registering BlockManager BlockManagerId(driver, cc4392cd314e, 34917, None)
23/07/25 07:16:08 INFO BlockManagerMasterEndpoint: Registering block manager cc4392cd314e:34917 with 366.3 MiB RAM, BlockManagerId(driver, cc4392cd314e, 34917, None)
23/07/25 07:16:08 INFO BlockManagerMaster: Registered BlockManager BlockManagerId(driver, cc4392cd314e, 34917, None)
23/07/25 07:16:08 INFO BlockManager: Initialized BlockManager: BlockManagerId(driver, cc4392cd314e, 34917, None)
```