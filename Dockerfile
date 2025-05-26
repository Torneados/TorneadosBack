# ==============================
# Etapa 1: Build con Maven
# ==============================
FROM maven:3.9.9-eclipse-temurin-8 AS builder
WORKDIR /app

# Copiamos pom y código fuente
COPY pom.xml .
COPY src ./src

# Construimos el jar y saltamos tests para agilizar
RUN mvn clean package -DskipTests

# ==============================
# Etapa 2: Imagen de ejecución
# ==============================
FROM eclipse-temurin:17-jre-alpine

# Creamos usuario no-root para mayor seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiamos el artefacto desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Ajustamos permisos
RUN chown appuser:appgroup app.jar

# Ejecutamos como usuario no-root
USER appuser

# Puerto por defecto de Spring Boot
EXPOSE 8080

# Punto de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]
