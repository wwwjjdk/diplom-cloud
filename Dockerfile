FROM openjdk:17-alpine
EXPOSE 9999
ADD target/refactordip-0.0.1-SNAPSHOT.jar /myapp.jar
ENTRYPOINT ["java", "-jar", "/myapp.jar"]