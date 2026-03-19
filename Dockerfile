# ETAPA 1: Compilación (Se hace en el PC o en GitHub Actions)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos solo el pom.xml primero para aprovechar la caché de Docker de las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Esto es lo que realmente correrá en tu NUC)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copiamos solo el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto de Spring Boot
EXPOSE 8080

# Comando para arrancar la API
ENTRYPOINT ["java", "-jar", "app.jar"]
