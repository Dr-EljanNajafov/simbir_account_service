FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY /build/libs/service.jar /app/accountService.jar
ENTRYPOINT ["java", "-jar", "accountService.jar"]