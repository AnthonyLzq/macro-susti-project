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

# Instalar maven
RUN apt-get update && \
  apt-get install -y --no-install-recommends \
  maven \
  && rm -rf /var/lib/apt/lists/*

# Instalar netcat
RUN apt-get update && \
  apt-get install -y --no-install-recommends \
  netcat \
  && rm -rf /var/lib/apt/lists/*

# Instalar zsh
RUN apt-get update && \
  apt-get install -y --no-install-recommends \
  zsh \
  && rm -rf /var/lib/apt/lists/*

# Instalar git
RUN apt-get update && \
  apt-get install -y --no-install-recommends \
  git \
  && rm -rf /var/lib/apt/lists/*

# Instalar oh-my-zsh
RUN sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"

# Instalar spaceship-prompt
RUN git clone https://github.com/spaceship-prompt/spaceship-prompt.git "/root/.oh-my-zsh/custom/themes/spaceship-prompt" --depth=1

# Crear un enlace simbólico
RUN ln -s "/root/.oh-my-zsh/custom/themes/spaceship-prompt/spaceship.zsh-theme" "/root/.oh-my-zsh/custom/themes/spaceship.zsh-theme"

# Setear el tema
RUN sed -i 's/ZSH_THEME="robbyrussell"/ZSH_THEME="spaceship"/g' "/root/.zshrc"
