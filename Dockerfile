FROM openjdk:22
WORKDIR /usr/app
COPY target/md-user-service-0.1.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "md-user-service-0.1.jar"]