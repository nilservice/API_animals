FROM openjdk:8-jdk-alpine as build
EXPOSE 8080
ADD target/API-0.0.1.jar api.jar
ENTRYPOINT ["java","-jar","api.jar"]