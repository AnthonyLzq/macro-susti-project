# Spark Cluster (Proyecto Final)

Proyecto final del curso de `Análisis en Macrodatos`.

## Requisitos

Tener [docker](https://docs.docker.com/desktop/install/linux-install/) y [docker-compose](https://docs.docker.com/compose/install/linux/) instalados en su sistema.

## Instalación

```sh
sh run.sh
```

## Ejecución

```sh
# Ingresar al contenedor que tiene spark
docker exec -it master-node bash

# Ejecutar cualquier archivo .scala
../bin/master-node --master spark://master-node:7077 # No está funcionando por el momento.
```

## Proyecto Java

La configuración actual del proyecto nos permite correr un solo archivo Java y sus dependencias en lugar de todo el proyecto. Esto es logrado gracias a la siguiente configuración en el [pom.xml](server/pom.xml):

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>exec-maven-plugin</artifactId>
      <version>1.6.0</version>
      <configuration>
        <!--Aquí cambiamos la clase que deseamos ejecutar-->
        <mainClass>macro.SparkApp</mainClass>
      </configuration>
    </plugin>
  </plugins>
</build>
```


```sh
# Movernos a la carpeta server
cd server

# Compilar el proyecto
mvn clean install

# Ejecutar la clase:
mvn exec:java
```
