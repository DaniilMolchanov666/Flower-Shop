FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} Flower_Shop-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["sudo", "java","-jar","Flower_Shop-0.0.1-SNAPSHOT.jar"]
CMD sudo docker image build . -t build/libs/flower_shop-0.0.1-snapshot.jar
