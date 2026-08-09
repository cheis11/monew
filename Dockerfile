FROM openjdk:17-slim
EXPOSE 8080
COPY app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
