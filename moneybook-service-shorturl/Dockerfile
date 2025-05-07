FROM openjdk:17-alpine

ARG stage
ENV SPRING_PROFILES_ACTIVE ${stage}

RUN apk add --no-cache bash curl

COPY ./build/libs/*.jar app.jar
RUN touch /app.jar

EXPOSE 8580 8580

# java -Xshare:dump

ENTRYPOINT [
    "java",
    "-Djava.security.egd=file:/dev/./urandom",
    "-XX:+UseG1GC",
    "-XX:TieredStopAtLevel=1",
    "-Xms4g",
    "-Xmx8g",
    "-jar",
    "/app.jar"
]
