FROM openjdk:17
ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar flowershop.jar
#VOLUME /Рабочий стол
#ARG JAR_FILE
#COPY ${JAR_FILE} Flower_Shop-0.0.1-SNAPSHOT.jar
#EXPOSE 8080
ENTRYPOINT ["sudo", "java","-jar","flowershop.jar"]
