FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-color --no-cache curl
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app.jar"]
