# Store Gradle cache so that it won't download the dependencies again and again
# This stage takes a little long time if the image cache is not yet available
FROM gradle:6.7.1-jdk11 AS cache
WORKDIR /opt/app
ENV GRADLE_USER_HOME /cache
COPY build.gradle.kts gradle.properties settings.gradle.kts ./
RUN gradle --no-daemon build --stacktrace

# Build stage
FROM gradle:6.7.1-jdk11 AS builder
WORKDIR /opt/app
COPY --from=cache /cache /home/gradle/.gradle
COPY . .
RUN gradle --no-daemon build --stacktrace
RUN ls -l /opt/app/build/libs/

# Runtime stage
FROM adoptopenjdk:11-jre-hotspot
WORKDIR /opt/app
COPY --from=builder /opt/app/build/libs/gradle-kotlin-sample-1.0-SNAPSHOT-all.jar /opt/app
ENTRYPOINT ["java", "-jar", "/opt/app/gradle-kotlin-sample-1.0-SNAPSHOT-all.jar"]