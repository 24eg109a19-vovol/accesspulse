# ==========================================
# AccessPulse - Production Multi-Stage Dockerfile
# ==========================================

# -----------------
# 1. Build Stage
# -----------------
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

# Copy Maven wrapper configuration and dependencies descriptor
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Fix CRLF line endings on Windows checkouts & make mvnw executable
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Resolve dependencies in a cache-friendly manner
RUN ./mvnw dependency:go-offline -B

# Copy project source files and build the production artifact
COPY src src
RUN ./mvnw clean package -DskipTests && \
    cp target/accesspulse-*.jar app.jar

# -----------------
# 2. Runtime Stage
# -----------------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root system user and groups for security
RUN addgroup --system --gid 1001 accesspulse && \
    adduser --system --uid 1001 --ingroup accesspulse accesspulse

# Create uploads directory with appropriate permissions
RUN mkdir -p /app/uploads && chown -R accesspulse:accesspulse /app

USER accesspulse:accesspulse

# Copy compiled jar from build stage
COPY --from=builder --chown=accesspulse:accesspulse /workspace/app.jar /app/app.jar

# Cloud PaaS like Render / Railway provide $PORT dynamically
ENV PORT=8080
EXPOSE 8080

# Production JVM options optimized for container environments
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
