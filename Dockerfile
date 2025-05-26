# ==============================
# Etapa 1: Build con Maven + JDK 21
# ==============================
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos pom y código fuente
COPY pom.xml .
COPY src ./src

# Construimos el jar (sin tests para agilizar)
RUN mvn clean package -DskipTests

# ==============================
# Etapa 2: Imagen de ejecución con JRE 21
# ==============================
FROM eclipse-temurin:21-jre-alpine

# Creamos usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiamos el artefacto
COPY --from=builder /app/target/*.jar app.jar

# Ajustamos permisos
RUN chown appuser:appgroup app.jar

# Ejecutamos como usuario no-root
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

