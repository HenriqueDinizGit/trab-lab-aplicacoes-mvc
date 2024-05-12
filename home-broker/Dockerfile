# Dockerfile para ambiente de build da aplicação
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN apt-get update && \
    apt-get install maven -y
RUN mvn clean package

# Segundo estágio do Dockerfile para montar o container de execução da aplicação
FROM eclipse-temurin:17-jre
WORKDIR /app
# Ensure to copy the correct JAR file from the build stage
COPY --from=build /app/target/home-broker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
