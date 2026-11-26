# Base image with Java 17 JRE
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the packaged JAR and static assets into the container
COPY target/feedback-app-1.0.0.jar app.jar
COPY index.html index.html

# Expose port (default 8080)
EXPOSE 8080
ENV PORT=8080

# Run the feedback application
ENTRYPOINT ["java", "-jar", "app.jar"]
