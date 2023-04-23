FROM openjdk:11.0.10-jre-slim-buster
ARG JAR_FILE=build/libs/*.jar
ENV JWT_SECRET_KEY="JWT_SECRET_KEY" \
    OPENAI_KEY="OPENAI_KEY"
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
