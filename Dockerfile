FROM maven:3.8.5-openjdk-17-slim

USER root
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    # Créer le répertoire .docker avec les bonnes permissions
    mkdir -p /root/.docker && \
    chmod 700 /root/.docker

WORKDIR /workspace


COPY . /workspace
CMD ["mvn", "clean", "package", "-DskipTests"]
