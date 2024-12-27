FROM maven:3.8.5-openjdk-17-slim

USER root

# Installer Docker
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    # Créer le répertoire .docker avec les bonnes permissions
    mkdir -p /root/.docker && \
    chmod 700 /root/.docker

# Passer à l'utilisateur Jenkins après avoir installé Docker
USER jenkins

WORKDIR /workspace

# Copier le contenu
COPY . /workspace

# Définir la commande par défaut pour Maven
CMD ["mvn", "clean", "package", "-DskipTests"]
