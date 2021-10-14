FROM openjdk:11

COPY ./target/registersystem-backend.jar /registersystem-backend.jar

ENTRYPOINT ["/usr/local/openjdk-11/bin/java", "-jar", "/registersystem-backend.jar"]
