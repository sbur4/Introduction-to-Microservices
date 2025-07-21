#Build stage
FROM gradle:8.14.3-jdk17-alpine AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN apk add --no-cache dos2unix && \
    dos2unix gradlew && \
    chmod +x gradlew && \
    ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew assemble --no-daemon -x test

#Runtime stage
FROM eclipse-temurin:17-jre-alpine AS run

WORKDIR /app
COPY --from=build /app/build/libs/*.jar song-service.jar
EXPOSE 8081

CMD ["java", "-jar", "song-service.jar"]