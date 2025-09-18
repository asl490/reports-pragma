# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY . .
RUN chmod +x gradlew

# Construye la aplicación Spring Boot en un JAR ejecutable
RUN ./gradlew clean build -x validateStructure

# Stage 2: Create the final image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crea un usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia el JAR construido desde la etapa 'builder'
COPY --from=builder /app/applications/app-service/build/libs/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

# Define el comando para ejecutar la aplicación cuando el contenedor se inicie
ENTRYPOINT ["java", "-jar", "app.jar"]
