# Этап сборки
FROM openjdk:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew build -x test

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /build/libs/service.jar /app/accountService.jar
ENTRYPOINT ["java", "-jar", "accountService.jar"]