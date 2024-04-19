ADD build/libs/Flower_Shop-0.0.1-SNAPSHOT.jar Flower_Shop-0.0.1-SNAPSHOT-plain.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "Flower_Shop-0.0.1-SNAPSHOT.jar"]