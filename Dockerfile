FROM openjdk:17
EXPOSE 8080
COPY target/MamataAgroAndDairyFarm.jar MamataAgroAndDairyFarm.jar
ENTRYPOINT ["java","-jar","/MamataAgroAndDairyFarm.jar"]