FROM openjdk:17
WORKDIR .
ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar flowershop.jar
ARG JAR_FILE
COPY ${JAR_FILE} Flower_Shop
EXPOSE 5432
COPY . .
ENTRYPOINT ["java","-jar","flowershop.jar"]

#FROM postgres:13.14-bullseye
#CMD sudo su
#CMD psql

