# =========================================================
# Imagem do APP (Java 21 / Spring Boot) - PetHub API
# Challenge FIAP Sprint 3 - DevOps Tools & Cloud Computing
#
# Multi-stage build: compila com Maven, roda com JRE enxuto.
# REQUISITO: o container NAO roda como root (USER springuser, uid 1001).
# =========================================================

# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Baixa dependencias numa camada separada para aproveitar cache do Docker
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -B -DskipTests clean package

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario e grupo sem privilegios administrativos
RUN addgroup -S spring && adduser -S springuser -u 1001 -G spring

COPY --from=builder /build/target/*.jar /app/app.jar
RUN chown -R springuser:spring /app

EXPOSE 8080

# Healthcheck via Actuator (usado pelo docker compose local)
HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=10 \
  CMD wget -q -O /dev/null http://localhost:8080/actuator/health || exit 1

# Troca para o usuario nao-root ANTES do entrypoint.
USER springuser

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
