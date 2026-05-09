FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY beneficio-contract ./beneficio-contract
COPY beneficio-ejb ./beneficio-ejb
COPY beneficio-api ./beneficio-api
RUN mvn -pl beneficio-api -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/beneficio-api/target/beneficio-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
