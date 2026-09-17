# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY discovery-server/pom.xml discovery-server/
COPY config-server/pom.xml config-server/
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY course-service/pom.xml course-service/
COPY ai-service/pom.xml ai-service/

COPY discovery-server/src discovery-server/src

RUN mvn -pl discovery-server -am package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl && \
    addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /build/discovery-server/target/*.jar app.jar
USER appuser
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
