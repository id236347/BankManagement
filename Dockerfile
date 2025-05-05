FROM openjdk:17-jdk
WORKDIR /app
COPY target/management-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
