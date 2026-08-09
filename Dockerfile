FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

FROM openjdk:17-slim
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
