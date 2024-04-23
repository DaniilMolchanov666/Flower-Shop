FROM openjdk:17
ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar flowershop.jar
VOLUME /Рабочий стол
ARG JAR_FILE
COPY ${JAR_FILE} Flower_Shop
EXPOSE 8000
ENTRYPOINT ["java","-jar","flowershop.jar"]
