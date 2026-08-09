FROM eclipse-temurin:17-jre
EXPOSE 8080
COPY app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
