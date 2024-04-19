FROM eclipse-temurin:17-jdk
ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "FROM eclipse-temurin:17-jdk"]
