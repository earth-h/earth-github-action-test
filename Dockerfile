# 베이스 이미지 + 이미지 별칭
FROM adoptopenjdk:11-jdk-hotspot AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM adoptopenjdk:11-jdk-hotspot
COPY --from=builder build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
#FROM adoptopenjdk:11-jdk-hotspot
#
#ARG JAR_FILE=build/libs/*.jar
#
#COPY ${JAR_FILE} app.jar
#
#ENTRYPOINT ["java", "-jar", "/app.jar"]
