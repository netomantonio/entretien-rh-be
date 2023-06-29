FROM openjdk:11-jdk-slim
ARG JAR_FILE=/build/libs/entretien-backend-app-0.0.1-SNAPSHOT.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
COPY ${JAR_FILE} app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./unrandom","-jar","/app.jar"]