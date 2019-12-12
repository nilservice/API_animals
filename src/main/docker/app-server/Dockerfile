
FROM openjdk:8-jdk-alpine as build

ADD app.jar .
EXPOSE 8080
RUN java -jar app.jar
CMD ["Server", "Spring-API"]