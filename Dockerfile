# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration first
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot JAR
RUN mvn clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the generated JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Render provides the PORT environment variable
EXPOSE 10000

# Start Spring Boot
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-10000}"]