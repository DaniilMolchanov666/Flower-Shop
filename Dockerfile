FROM openjdk:17
WORKDIR . .
ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar flowershop.jar
ARG JAR_FILE
COPY ${JAR_FILE} Flower_Shop
COPY ./flowers/ ./flowers
COPY ./src/main/resources/application.log ./application.log
EXPOSE 8000
ENTRYPOINT ["java","-jar","flowershop.jar"]


