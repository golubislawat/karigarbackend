# Stage 1: Build the JAR with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render assigns dynamic PORT environment variable (default 8080 locally, 10000 on Render)
ENV PORT=8080
EXPOSE 8080

# JVM memory tuning for Render Free Tier (512MB RAM):
# -XX:+UseSerialGC: lowers GC memory overhead significantly
# -XX:MaxRAMPercentage=75.0: keeps heap within container memory limits to avoid OOM
ENTRYPOINT ["sh", "-c", "java -XX:+UseSerialGC -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT} -jar app.jar"]
