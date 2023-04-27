FROM openjdk:11.0.10-jre-slim-buster
ARG JAR_FILE=build/libs/*.jar
ARG JWT_SECRET_KEY
ARG OPENAI_KEY
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENV OPENAI_KEY=${OPENAI_KEY}
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
