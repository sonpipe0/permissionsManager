FROM gradle:8.10.1-jdk21 AS builder
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:21-jdk
COPY --from=builder /home/gradle/src/build/libs/permissionsManager-0.0.1-SNAPSHOT.jar /app/permissionsManager.jar
COPY newrelic-java/newrelic /app/newrelic/
ARG NEW_RELIC_LICENSE_KEY
ENV NEW_RELIC_APP_NAME="permissionsManager"
ENV NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "/app/permissionsManager.jar"]
