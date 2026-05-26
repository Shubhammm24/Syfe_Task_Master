# Stage 1: Build stage
FROM gradle:8.7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
