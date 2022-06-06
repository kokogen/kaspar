FROM openjdk:11
COPY target/kaspar-0.0.1.jar /opt/app.jar
#RUN apt-get update && apt-get install -y iputils-ping
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/opt/app.jar"]