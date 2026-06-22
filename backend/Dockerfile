# ---- Etape 1 : build de l'artefact Java avec Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Mise en cache des dependances
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Etape 2 : image d'execution legere (JRE seul) ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/pmt-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
