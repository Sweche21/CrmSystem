FROM openjdk:8-jdk as build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]