FROM openjdk:17
EXPOSE 8080
COPY target/mamataagroanddairyfarm.jar mamataagroanddairyfarm.jar
ENTRYPOINT ["java","-jar","/mamataagroanddairyfarm.jar"]