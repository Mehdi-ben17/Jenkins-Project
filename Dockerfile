FROM maven:3.8.5-openjdk-17-slim

# Copy application files
COPY . /workspace
WORKDIR /workspace

# Build the application
RUN mvn clean package -DskipTests

# Use multi-stage build for smaller final image
FROM openjdk:17-slim
COPY --from=0 /workspace/target/*.jar /app/application.jar
WORKDIR /app

# Run the application
CMD ["java", "-jar", "application.jar"]