FROM openjdk:22
WORKDIR /usr/app
COPY target/md-user-service-0.1.jar .
ENV AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "md-user-service-0.1.jar"]