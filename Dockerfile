FROM maven:3.8.5-openjdk-17-slim

# Install Docker and required dependencies
RUN apt-get update && \
    apt-get install -y \
    docker.io \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create Jenkins user and set up permissions
RUN useradd -m -d /home/jenkins -s /bin/bash jenkins && \
    usermod -aG docker jenkins

# Switch to Jenkins user
USER jenkins
WORKDIR /workspace

# Copy application files
COPY --chown=jenkins:jenkins . /workspace

# Default command
CMD ["mvn", "clean", "package", "-DskipTests"]