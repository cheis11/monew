# 1단계: 빌드 환경 (Gradle 빌드)
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# 2단계: 실행 환경
FROM openjdk:17-slim
EXPOSE 8080
# 스프링 부트 실행 jar 파일만 복사 (에러 원인 해결)
COPY --from=build /home/gradle/src/build/libs/Monew-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
