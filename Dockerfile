FROM eclipse-temurin:26-jdk AS build
WORKDIR /workspace
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN ./gradlew --version
COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:26-jre
WORKDIR /app
RUN useradd --no-create-home --shell /usr/sbin/nologin appuser
COPY --from=build /workspace/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
