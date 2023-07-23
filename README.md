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
../bin/master-node --master spark://master-node:7077
```
