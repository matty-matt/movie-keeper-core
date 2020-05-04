FROM maven:3.6.3-jdk-11-slim as maven
COPY pom.xml /tmp
COPY src /tmp/src
WORKDIR /tmp
RUN mvn clean package -DskipTests

FROM adoptopenjdk:11-jre-hotspot
COPY --from=maven /tmp/target/*.jar /usr/src/trailerservice/app.jar
WORKDIR /usr/src/trailerservice

EXPOSE 35003
CMD ["java", "-jar", "app.jar"]