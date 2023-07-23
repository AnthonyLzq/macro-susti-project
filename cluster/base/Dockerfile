# Establecer la imagen base
FROM apache/spark:v3.2.1

USER root

# Actualizar e instalar dependencias
RUN apt-get update && \
  apt-get install -y --no-install-recommends \
  ca-certificates \
  curl \
  gnupg2 \
  && rm -rf /var/lib/apt/lists/*

# Agregar el repositorio 'adoptopenjdk' que contiene OpenJDK 8
RUN echo 'deb http://adoptopenjdk.jfrog.io/adoptopenjdk/deb buster main' | tee /etc/apt/sources.list.d/adoptopenjdk.list

# Importar la clave GPG del repositorio
RUN curl -L https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | apt-key add -

# Instalar OpenJDK 8
RUN apt-get update && \
  apt-get install -y adoptopenjdk-8-hotspot && \
  apt-get clean

# Configurar las variables de entorno
ENV JAVA_HOME /usr/lib/jvm/adoptopenjdk-8-hotspot-amd64
ENV PATH $JAVA_HOME/bin:$PATH

EXPOSE 4040
EXPOSE 8080
EXPOSE 7077
EXPOSE 9999

# Agregar la carpeta conf
RUN mkdir /opt/spark/conf
ADD ./conf /opt/spark/conf

# Instalar maven:
RUN apt-get update && \
  apt-get install -y --no-install-recommends \
  maven \
  && rm -rf /var/lib/apt/lists/*

