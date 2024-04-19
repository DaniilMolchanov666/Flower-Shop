FROM eclipse-temurin:17-jdk
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} Flower_Shop-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["gradle installDist"]
