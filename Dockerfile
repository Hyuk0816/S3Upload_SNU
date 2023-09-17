FROM openjdk:11.0.11-jre-slim
RUN echo "test,,,,"
ARG JAR_FILE=build/libs/S3Upload_SNU-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
