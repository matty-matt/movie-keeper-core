FROM alpine as downloader
RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/c096cface5fbd9f2d6b037391dfecae6fde1362e/wait-for-it.sh

FROM maven:3.6.3-jdk-11-slim as maven
COPY pom.xml /tmp
COPY src /tmp/src
WORKDIR /tmp
RUN mvn clean package -DskipTests

FROM adoptopenjdk:11-jre-hotspot
COPY --from=maven /tmp/target/*.jar /usr/src/movieservice/app.jar
COPY --from=downloader ./wait-for-it.sh /usr/src/movieservice
RUN chmod +x /usr/src/movieservice/wait-for-it.sh
WORKDIR /usr/src/movieservice

EXPOSE 35001
CMD ["java", "-jar", "app.jar"]