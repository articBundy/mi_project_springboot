# ETAPA 1: Compilación (Build)
# Usamos una imagen de Maven que ya incluya JDK 25
FROM maven:3.9.6-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Runtime)
# Usamos una imagen ligera de JRE o JDK 25 para correr la app
FROM eclipse-temurin:25-jre-alpine
# O puedes usar la de Microsoft si prefieres: mcr.microsoft.com/openjdk/jdk:25-ubuntu

WORKDIR /app
COPY --from=build /target/*.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]