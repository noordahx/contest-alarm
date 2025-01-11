FROM openjdk:17-jdk-slim

WORKDIR /app

COPY contest-alarm-assembly-1.0.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]