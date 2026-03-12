# syntax=docker/dockerfile:1

# Simple multi-stage Dockerfile:
# - Build stage: use official Maven image to compile and package the app
# - Runtime stage: use a small JRE image to run the resulting jar

FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy only what we need to build (pom and source). This keeps the build cache effective.
COPY pom.xml ./
COPY src ./src

# Build the application (skip tests for faster builds; remove -DskipTests if you want tests to run)
RUN mvn -B -DskipTests package

# --- Runtime image ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Allow overriding the JAR file name at build time if needed
ARG JAR_FILE=target/*.jar

# Copy the jar produced in the builder stage and rename to app.jar
COPY --from=builder /app/${JAR_FILE} /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
