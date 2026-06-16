# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven files first for better Docker layer caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]