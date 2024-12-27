# Utilisation de l'image Maven avec OpenJDK 17
FROM maven:3.8.5-openjdk-17-slim

# Passer à l'utilisateur root pour installer Docker
USER root

# Installer Docker CLI
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    # Créer le répertoire .docker avec les bonnes permissions
    mkdir -p /root/.docker && \
    chmod 700 /root/.docker

# Définir le répertoire de travail
WORKDIR /workspace

# Copier tout le contenu de l'hôte vers le conteneur
COPY . /workspace

# Exécuter Maven par défaut
CMD ["mvn", "clean", "package", "-DskipTests"]
