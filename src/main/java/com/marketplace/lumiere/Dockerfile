# ---- Build stage: compile the app with Maven ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the Maven files and download dependencies first (faster rebuilds)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN mvn dependency:go-offline -B || true

# Copy the source and build the jar (skip tests for a faster deploy)
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Run stage: run the built jar ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render provides the PORT env var; Spring Boot must listen on it.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]