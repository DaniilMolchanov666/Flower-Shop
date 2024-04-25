FROM openjdk:17
WORKDIR .
ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar flowershop.jar
ARG JAR_FILE
COPY ${JAR_FILE} Flower_Shop
EXPOSE 8090
COPY . .
ENTRYPOINT ["java","-jar","flowershop.jar"]
