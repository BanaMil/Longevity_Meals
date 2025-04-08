# Use Java 21 base image
FROM eclipse-temurin:21-jdk

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src/ src/

# Set executable permissions for gradlew
RUN chmod +x gradlew

# Build the application and run it
RUN ./gradlew build

# Run the application
ENTRYPOINT ["java", "-jar", "build/libs/*.jar"]
