FROM openjdk:17-alpine

ARG stage
ENV SPRING_PROFILES_ACTIVE ${stage}

RUN apk add --no-cache bash curl

COPY ./build/libs/*.jar app.jar
RUN touch /app.jar

EXPOSE 8480 18480

ENTRYPOINT [
    "java",
    "-Djava.security.egd=file:/dev/./urandom",
    "-Duser.timezone=Asia/Seoul",
    "-XX:+UseG1GC",
    "-XX:MaxGCPauseMillis=200",
    "-XX:InitiatingHeapOccupancyPercent=45",
    "-Xms4g",
    "-Xmx8g",
    "-XX:+HeapDumpOnOutOfMemoryError",
    "-XX:HeapDumpPath=/var/log/java_heapdump.hprof",
    "-verbose:gc",
    "-XX:+PrintGCDetails",
    "-XX:+PrintGCDateStamps",
    "-jar",
    "/app.jar"
]
