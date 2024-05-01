FROM openjdk:17
WORKDIR ./scr
ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar flowershop.jar
ARG JAR_FILE
COPY ${JAR_FILE} Flower_Shop
EXPOSE 8000
COPY . .
ENTRYPOINT ["java","-jar","flowershop.jar"]


