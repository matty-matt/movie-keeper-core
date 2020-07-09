FROM alpine as downloader
RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/c096cface5fbd9f2d6b037391dfecae6fde1362e/wait-for-it.sh

FROM maven:3.6.3-jdk-11-slim as maven
COPY pom.xml /tmp
COPY src /tmp/src
WORKDIR /tmp
RUN mvn clean package -DskipTests

FROM adoptopenjdk:11-jre-hotspot
COPY --from=maven /tmp/target/*.jar /usr/src/digital-release-tracker/app.jar
COPY --from=downloader ./wait-for-it.sh /usr/src/digital-release-tracker
RUN chmod +x /usr/src/digital-release-tracker/wait-for-it.sh
WORKDIR /usr/src/digital-release-tracker

EXPOSE 35001
CMD ["java", "-jar", "app.jar"]